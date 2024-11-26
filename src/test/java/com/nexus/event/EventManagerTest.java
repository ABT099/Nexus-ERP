package com.nexus.event;

import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationHolderDto;
import com.nexus.notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventManagerTest {

    @Mock
    private NotificationManager notificationManager;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    @InjectMocks
    private EventManager eventManager;

    @BeforeEach
    void setUp() {
        EventManager.getAdminEvents().clear();
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();  // Execute the task synchronously in the test
            return null;  // Void return type
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test
    void addEvent_addsEventAndSendsReminder() {
        // Arrange
        Long adminId = 1L;
        Instant now = Instant.now().plusSeconds(3600);
        EventHolderDto event = new EventHolderDto(1, "Test Event", now.atZone(ZoneId.systemDefault()), false);

        // Act
        eventManager.addEvent(adminId, event);

        // Assert
        ConcurrentSkipListSet<EventHolderDto> events = EventManager.getAdminEvents().get(adminId);
        assertNotNull(events);
        assertTrue(events.contains(event));

        // Verify notification is sent
        ArgumentCaptor<NotificationHolderDto> notificationCaptor = ArgumentCaptor.forClass(NotificationHolderDto.class);
        verify(notificationManager, atLeastOnce()).addNotification(notificationCaptor.capture());
        NotificationHolderDto notification = notificationCaptor.getValue();
        assertEquals("Event Reminder", notification.getTitle());
        assertEquals(NotificationType.REMINDER, notification.getType());
    }

    @Test
    void removeEvent_removesEvent() {
        // Arrange
        Long adminId = 1L;
        Instant now = Instant.now().plusSeconds(3600);
        EventHolderDto event = new EventHolderDto(1, "Test Event", now.atZone(ZoneId.systemDefault()), false);

        EventManager.getAdminEvents().computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>()).add(event);

        // Act
        eventManager.removeEvent(adminId, event);

        // Assert
        ConcurrentSkipListSet<EventHolderDto> events = EventManager.getAdminEvents().get(adminId);
        assertTrue(events == null || !events.contains(event));
    }

    @Test
    void updateUrgentStatus_marksEventsAsUrgent() {
        // Arrange
        Long adminId = 1L;
        Instant now = Instant.now();
        EventHolderDto pastEvent = new EventHolderDto(1, "Past Event", now.minusSeconds(3600).atZone(ZoneId.systemDefault()), false);
        EventHolderDto futureEvent = new EventHolderDto(2, "Future Event", now.plusSeconds(3800).atZone(ZoneId.systemDefault()), false);

        ConcurrentSkipListSet<EventHolderDto> events = new ConcurrentSkipListSet<>();
        events.add(pastEvent);
        events.add(futureEvent);

        EventManager.getAdminEvents().put(adminId, events);

        // Act
        eventManager.scheduledUrgentCheck();

        // Assert
        assertTrue(pastEvent.isUrgent());
        assertFalse(events.contains(futureEvent));

        // Verify database update
        verify(eventRepository, atLeastOnce()).updateUrgentToTrue(Set.of(pastEvent.getEventId()));
    }
}
