package com.nexus.event;

import com.nexus.admin.Admin;
import com.nexus.admin.AdminService;
import com.nexus.common.Status;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.utils.UpdateHandler;
import com.nexus.zoned.Zoned;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("events")
public class EventController {

    private final EventRepository eventRepository;
    private final AdminService adminService;
    private final EventManager eventManager;
    private final EventMapper eventMapper;
    private final MonitorManager monitorManager;

    public EventController(
            EventRepository eventRepository,
            AdminService adminService,
            EventManager eventManager,
            EventMapper eventMapper,
            MonitorManager monitorManager
    ) {
        this.eventRepository = eventRepository;
        this.adminService = adminService;
        this.eventManager = eventManager;
        this.eventMapper = eventMapper;
        this.monitorManager = monitorManager;
    }

    @Zoned
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<BasicEventResponse>> getAllByAdmin(
            @Valid @Positive @PathVariable long adminId,
            @RequestParam(
                    required = false,
                    name = "a"
            ) String archived
    ) {

        List<Event> events;

        if (Objects.equals(archived.toLowerCase(), "archived")) {
            events = eventRepository.findAllByAdminId(adminId);
        } else {
            events = eventRepository.findAllNonArchivedByAdminId(adminId);
        }

        events.sort(
                Comparator.comparing(Event::isUrgent, Comparator.nullsLast(Boolean::compareTo)).reversed()
                        .thenComparing(Event::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        return ResponseEntity.ok(events.stream().map(eventMapper::toBasicEventResponse).toList());
    }

    @Zoned
    @GetMapping("{id}")
    public ResponseEntity<EventResponse> getById(@Valid @Positive @PathVariable int id) {
        Event event = findById(id);

        return ResponseEntity.ok(eventMapper.toEventResponse(event));
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CreateEventRequest request) {
        Event event = new Event(request.name(), request.description(), request.type(), request.date());

        List<Admin> admins = adminService.findAllById(request.adminIds());

        for (Admin admin : admins) {
            event.addAdmin(admin);
        }

        Event savedEvent = eventRepository.save(event);
        eventManager.addEvent(
                request.adminIds(),
                new EventDTO(Objects.requireNonNull(savedEvent.getId()), savedEvent.getName(), savedEvent.getDate(), savedEvent.isUrgent())
        );

        monitorManager.monitor(savedEvent, ActionType.CREATE);

        return ResponseEntity.created(URI.create("events/" + savedEvent.getId()))
                .body(savedEvent.getId());
    }

    @PutMapping("{id}")
    public void update(
            @Valid @Positive @PathVariable int id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        Event event = findById(id);

        if (event.isArchived()) {
            throw new NoUpdateException("Archived event cannot be updated");
        }

        UpdateHandler.updateEntity(event, tracker -> {
            tracker.updateField(event::getName, request.name(), event::setName);
            tracker.updateField(event::getDescription, request.description(), event::setDescription);
            tracker.updateField(event::getType, request.type(), event::setType);
            tracker.updateField(event::getStatus, request.status(), event::setStatus);
            tracker.updateField(event::getDate, request.date(), event::setDate);
        }, () -> eventRepository.save(event), monitorManager);
    }

    @PatchMapping("{id}/status")
    public void updateStatus(@Valid @Positive @PathVariable int id, @RequestBody Status status) {
        Event event = findById(id);

        if (event.isArchived()) {
            throw new NoUpdateException("Archived event cannot be updated");
        }

        UpdateHandler.updateEntity(event, tracker -> {
            tracker.updateField(event::getStatus, status, event::setStatus);
        }, () -> eventRepository.save(event), monitorManager);
    }

    @PatchMapping("{eventId}/add-admin/{adminId}")
    public void addAdmin(
            @Valid @Positive @PathVariable long eventId,
            @Valid @Positive @PathVariable long adminId
    ) {
        Event event = findById(eventId);

        if (event.isArchived()) {
            throw new NoUpdateException("Archived event cannot be updated");
        }

        Admin admin = adminService.findById(adminId);

        event.addAdmin(admin);
        eventRepository.save(event);

        eventManager.addEvent(adminId, new EventDTO(eventId, event.getName(), event.getDate(), event.isUrgent()));

        monitorManager.monitor(event, ActionType.ADD_ADMIN, admin.getUser().getUsername());
    }

    @PatchMapping("{eventId}/remove-admin/{adminId}")
    public void removeAdmin(
            @Valid @Positive @PathVariable long eventId,
            @Valid @Positive @PathVariable long adminId
    ) {
        Event event = findById(eventId);

        if (event.isArchived()) {
            throw new NoUpdateException("Archived event cannot be updated");
        }

        Admin admin = adminService.findById(adminId);

        event.removeAdmin(admin);
        eventRepository.save(event);

        eventManager.removeEvent(adminId, new EventDTO(eventId, event.getName(), event.getDate(), event.isUrgent()));

        monitorManager.monitor(event, ActionType.REMOVE_ADMIN, admin.getUser().getUsername());
    }

    @PatchMapping("archive/{id}")
    public void archive(@Valid @Positive @PathVariable long id) {
        Event event = findById(id);

        if (event.isArchived()) {
            throw new NoUpdateException("Event is already archived");
        }

        event.setArchived(true);

        eventRepository.save(event);

        monitorManager.monitor(event, ActionType.ARCHIVE);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));

        Set<Long> adminIds = event.getAdmins().stream()
                .map(Admin::getId)
                .collect(Collectors.toSet());

        EventDTO eventDTO = new EventDTO(event.getId(), event.getName(), event.getDate(), event.isUrgent());

        eventManager.removeEvent(adminIds, eventDTO);

        List<Admin> adminsToRemove = new ArrayList<>(event.getAdmins());
        for (Admin admin : adminsToRemove) {
            admin.removeEvent(event);
        }
        event.getAdmins().clear();

        eventRepository.deleteById(id);

        monitorManager.monitor(event, ActionType.DELETE);
    }

    private Event findById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("event with id " + id + " not found")
                );
    }
}
