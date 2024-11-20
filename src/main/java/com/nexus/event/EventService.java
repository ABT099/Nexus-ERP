package com.nexus.event;

import com.nexus.admin.Admin;
import com.nexus.admin.AdminService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AdminService adminService;
    private final EventHandler eventHandler;

    public EventService(EventRepository eventRepository, AdminService adminService, EventHandler eventHandler) {
        this.eventRepository = eventRepository;
        this.adminService = adminService;
        this.eventHandler = eventHandler;
    }

    public List<Event> findAllByAdmin(long adminId) {
        Set<Integer> ids = eventHandler.getEventsForAdmin(adminId);
        return eventRepository.findAllById(ids);
    }

    public Event findById(int id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("event with id " + id + " not found"));
    }

    @Transactional
    public void save(CreateEventRequest request) {
        Event event = new Event(request.name(), request.description(), request.type());

        List<Admin> admins = adminService.getAllByIds(request.adminIds());

        for (Admin admin : admins) {
            event.addAdmin(admin);
        }

        eventRepository.save(event);

        eventHandler.addEvent(
                request.adminIds(),
                new EventHolderDto(Objects.requireNonNull(event.getId()), event.getName(), event.getDate(), event.isUrgent()));
    }

    @Transactional
    public void update(int id, UpdateEventRequest request) {
        Event event = findById(id);

        boolean updated = false;

        if (!Objects.equals(event.getName(), request.name())) {
            event.setName(request.name());
            updated = true;
        }

        if (!Objects.equals(event.getDescription(), request.description())) {
            event.setDescription(request.description());
            updated = true;
        }

        if (!Objects.equals(event.getType(), request.type())) {
            event.setType(request.type());
            updated = true;
        }

        if (!Objects.equals(event.getType(), request.type())) {
            event.setType(request.type());
            updated = true;
        }

        if (!updated) {
            throw new NoUpdateException("no changes were made");
        }

        eventRepository.save(event);
    }

    @Transactional
    public void addAdmin(long adminId, int eventId) {
        Event event = findById(eventId);
        Admin admin = adminService.getById(adminId);

        event.addAdmin(admin);
        eventRepository.save(event);

        eventHandler.addEvent(adminId, new EventHolderDto(eventId, event.getName(), event.getDate(), event.isUrgent()));
    }

    @Transactional
    public void removeAdmin(long adminId, int eventId) {
        Event event = findById(eventId);
        Admin admin = adminService.getById(adminId);

        event.removeAdmin(admin);
        eventRepository.save(event);

        eventHandler.removeEvent(adminId, new EventHolderDto(eventId, event.getName(), event.getDate(), event.isUrgent()));
    }

    @Transactional
    public void remove(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));

        Set<Long> adminIds = event.getAdmins().stream()
                .map(Admin::getId)
                .collect(Collectors.toSet());

        EventHolderDto eventHolderDto = new EventHolderDto(event.getId(), event.getName(), event.getDate(), event.isUrgent());

        eventHandler.removeEvent(adminIds, eventHolderDto);

        for (Admin admin : event.getAdmins()) {
            admin.removeEvent(event);
        }
        event.getAdmins().clear();

        eventRepository.deleteById(id);
    }
}
