package com.nexus.event;

import com.nexus.admin.Admin;
import com.nexus.admin.AdminService;
import com.nexus.common.Status;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventManager eventManager;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private EventService eventService;
    @Test
    void findAllByAdmin_shouldReturnSortedEvents() {
        List<Event> events = List.of(
                new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1)),
                new Event("Event2", "Description2", EventType.MEETING, ZonedDateTime.now().plusDays(1))
        );

        events.getFirst().setDate(ZonedDateTime.now().plusDays(1));
        events.get(1).setDate(ZonedDateTime.now().plusDays(2));

        when(eventRepository.findAllByAdminId(1L)).thenReturn(events);

        List<Event> result = eventService.findAllByAdmin(1L);

        assertEquals(events, result);
    }

    @Test
    void findById_shouldReturnEvent_whenEventExists() {
        Event event = new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        Event result = eventService.findById(1);

        assertEquals(event, result);
    }

    @Test
    void findById_shouldThrowException_whenEventDoesNotExist() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.findById(1));
    }

    @Test
    void save_shouldSaveEvent() {
        CreateEventRequest request = new CreateEventRequest(Set.of(2L, (1L)), "Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));

        List<Admin> admins = List.of(new Admin(), new Admin());
        when(adminService.findAllById(request.adminIds())).thenReturn(admins);

        Event event = new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));
        event.setId(1);

        doReturn(event).when(eventRepository).save(any(Event.class));

        eventService.save(request);

        verify(eventRepository).save(any(Event.class));
        verify(eventManager).addEvent(eq(request.adminIds()), any(EventHolderDto.class));
    }

    @Test
    void update_shouldUpdateEvent_whenChangesMade() {
        UpdateEventRequest request = new UpdateEventRequest("UpdatedEvent", "UpdatedDescription", EventType.MEETING, Status.PENDING, ZonedDateTime.now().plusDays(2));
        Event event = new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        eventService.update(1, request);

        verify(eventRepository).save(event);
    }

    @Test
    void update_shouldThrowException_whenNoChangesMade() {
        UpdateEventRequest request = new UpdateEventRequest("Event1", "Description1", EventType.MEETING, Status.PENDING, ZonedDateTime.now().plusDays(1));
        Event event = new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        assertThrows(NoUpdateException.class, () -> eventService.update(1, request));
    }

    @Test
    void addAdmin_shouldAddAdminToEvent() {
        Event event = new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));
        Admin admin = new Admin();
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(adminService.findById(1L)).thenReturn(admin);

        eventService.addAdmin(1L, 1);

        verify(eventRepository).save(event);
        verify(eventManager).addEvent(eq(1L), any(EventHolderDto.class));
    }

    @Test
    void removeAdmin_shouldRemoveAdminFromEvent() {
        Event event = new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));
        Admin admin = new Admin();
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(adminService.findById(1L)).thenReturn(admin);

        eventService.removeAdmin(1L, 1);

        verify(eventRepository).save(event);
        verify(eventManager).removeEvent(eq(1L), any(EventHolderDto.class));
    }

    @Test
    void remove_shouldDeleteEvent() {
        Event event = new Event("Event1", "Description1", EventType.MEETING, ZonedDateTime.now().plusDays(1));
        Admin admin = new Admin();
        event.addAdmin(admin);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        eventService.remove(1);

        verify(eventRepository).deleteById(1);
        verify(eventManager).removeEvent(anySet(), any(EventHolderDto.class));
    }
}
