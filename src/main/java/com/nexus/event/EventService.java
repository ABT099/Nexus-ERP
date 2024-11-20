package com.nexus.event;

import com.nexus.admin.Admin;
import com.nexus.admin.AdminService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AdminService adminService;

    public EventService(EventRepository eventRepository, AdminService adminService) {
        this.eventRepository = eventRepository;
        this.adminService = adminService;
    }

    public List<Event> findAllByAdmin(long adminId) {
        return eventRepository.findAllByAdminId(adminId);
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
    }

    @Transactional
    public void removeAdmin(long adminId, int eventId) {
        Event event = findById(eventId);
        Admin admin = adminService.getById(adminId);

        event.removeAdmin(admin);
        eventRepository.save(event);
    }

    @Transactional
    public void remove(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));

        for (Admin admin : event.getAdmins()) {
            admin.removeEvent(event);
        }
        event.getAdmins().clear();

        eventRepository.deleteById(id);
    }
}
