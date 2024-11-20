package com.nexus.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<Event>> getAllByAdmin(
            @Valid
            @Positive
            @PathVariable long adminId) {
        return ResponseEntity.ok(eventService.findAllByAdmin(adminId));
    }

    @GetMapping("{id}")
    public ResponseEntity<Event> getById(
            @Valid
            @Positive
            @PathVariable int id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PostMapping
    public void create(@Valid CreateEventRequest request) {
        eventService.save(request);
    }

    @PutMapping("{id}")
    public void update(
            @Valid
            @Positive
            @PathVariable int id,
            UpdateEventRequest request) {
        eventService.update(id, request);
    }

    @PatchMapping("{eventId}/add-admin/{adminId}")
    public void addAdmin(
            @Valid
            @Positive
            @PathVariable int eventId,
            @Valid
            @Positive
            @PathVariable long adminId) {
        eventService.addAdmin(adminId, eventId);
    }

    @PatchMapping("{eventId}/remove-admin/{adminId}")
    public void removeAdmin(
            @Valid
            @Positive
            @PathVariable int eventId,
            @Valid
            @Positive
            @PathVariable long adminId) {
        eventService.removeAdmin(adminId, eventId);
    }

    @DeleteMapping("{id}")
    public void delete(
            @Valid
            @Positive
            @PathVariable int id) {
        eventService.remove(id);
    }
}
