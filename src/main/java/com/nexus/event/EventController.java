package com.nexus.event;

import com.nexus.admin.Admin;
import com.nexus.admin.AdminFinder;
import com.nexus.common.Status;
import com.nexus.exception.ResourceNotFoundException;
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
    private final AdminFinder adminFinder;
    private final EventManager eventManager;

    public EventController(EventRepository eventRepository, AdminFinder adminFinder, EventManager eventManager) {
        this.eventRepository = eventRepository;
        this.adminFinder = adminFinder;
        this.eventManager = eventManager;
    }

    @Zoned
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<Event>> getAllByAdmin(@Valid @Positive @PathVariable long adminId) {
        List<Event> events = new ArrayList<>(Optional.ofNullable(eventRepository.findAllByAdminId(adminId))
                .orElse(Collections.emptyList()));

        events.sort(
                Comparator.comparing(Event::isUrgent, Comparator.nullsLast(Boolean::compareTo)).reversed()
                        .thenComparing(Event::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        return ResponseEntity.ok(events);
    }

    @Zoned
    @GetMapping("{id}")
    public ResponseEntity<Event> getById(@Valid @Positive @PathVariable int id) {
        return ResponseEntity.ok(findById(id));
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody CreateEventRequest request) {
        Event event = new Event(request.name(), request.description(), request.type(), request.date());

        List<Admin> admins = adminFinder.findAllById(request.adminIds());

        for (Admin admin : admins) {
            event.addAdmin(admin);
        }

        Event savedEvent = eventRepository.save(event);
        eventManager.addEvent(
                request.adminIds(),
                new EventHolderDto(Objects.requireNonNull(savedEvent.getId()), savedEvent.getName(), savedEvent.getDate(), savedEvent.isUrgent())
        );

        return ResponseEntity.created(URI.create("events/" + savedEvent.getId()))
                .body(savedEvent.getId());
    }

    @PutMapping("{id}")
    public void update(
            @Valid @Positive @PathVariable int id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        Event event = findById(id);

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(event::getName, request.name(), event::setName);
            tracker.updateField(event::getDescription, request.description(), event::setDescription);
            tracker.updateField(event::getType, request.type(), event::setType);
            tracker.updateField(event::getStatus, request.status(), event::setStatus);
            tracker.updateField(event::getDate, request.date(), event::setDate);
        }, () -> eventRepository.save(event));
    }

    @PatchMapping("{id}/status")
    public void updateStatus(@Valid @Positive @PathVariable int id, @RequestBody Status status) {
        Event event = findById(id);

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(event::getStatus, status, event::setStatus);
        }, () -> eventRepository.save(event));
    }

    @PatchMapping("{eventId}/add-admin/{adminId}")
    public void addAdmin(
            @Valid @Positive @PathVariable int eventId,
            @Valid @Positive @PathVariable long adminId
    ) {
        Event event = findById(eventId);
        Admin admin = adminFinder.findById(adminId);

        event.addAdmin(admin);
        eventRepository.save(event);

        eventManager.addEvent(adminId, new EventHolderDto(eventId, event.getName(), event.getDate(), event.isUrgent()));

    }

    @PatchMapping("{eventId}/remove-admin/{adminId}")
    public void removeAdmin(
            @Valid @Positive @PathVariable int eventId,
            @Valid @Positive @PathVariable long adminId
    ) {
        Event event = findById(eventId);
        Admin admin = adminFinder.findById(adminId);

        event.removeAdmin(admin);
        eventRepository.save(event);

        eventManager.removeEvent(adminId, new EventHolderDto(eventId, event.getName(), event.getDate(), event.isUrgent()));
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));

        Set<Long> adminIds = event.getAdmins().stream()
                .map(Admin::getId)
                .collect(Collectors.toSet());

        EventHolderDto eventHolderDto = new EventHolderDto(event.getId(), event.getName(), event.getDate(), event.isUrgent());

        eventManager.removeEvent(adminIds, eventHolderDto);

        List<Admin> adminsToRemove = new ArrayList<>(event.getAdmins());
        for (Admin admin : adminsToRemove) {
            admin.removeEvent(event);
        }
        event.getAdmins().clear();

        eventRepository.deleteById(id);
    }

    private Event findById(int id) {
        return eventRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("event with id " + id + " not found")
                );
    }
}
