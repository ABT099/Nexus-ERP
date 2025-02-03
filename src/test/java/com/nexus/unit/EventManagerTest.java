package com.nexus.unit;

import com.nexus.event.EventDTO;
import com.nexus.event.EventManager;
import com.nexus.event.EventRepository;
import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationDTO;
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
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

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
        lenient().doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();  // Execute the task synchronously in the test
            return null;  // Void return type
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test
    void addEvent_addsEventAndSendsReminder() throws InterruptedException {
        // Arrange
        Long adminId = 1L;
        Instant now = Instant.now().plusSeconds(3600);
        EventDTO event = new EventDTO(1L, "Test Event", now, false);
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            latch.countDown();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        // Act
        eventManager.addEvent(adminId, event);
        latch.await();  // Wait for the async task to complete

        // Assert
        ConcurrentSkipListSet<EventDTO> events = EventManager.getAdminEvents().get(adminId);
        assertNotNull(events);

        // Verify notification is sent
        ArgumentCaptor<NotificationDTO> notificationCaptor = ArgumentCaptor.forClass(NotificationDTO.class);
        verify(notificationManager, atLeastOnce()).addNotification(notificationCaptor.capture());
        NotificationDTO notification = notificationCaptor.getValue();
        assertEquals("Event Reminder", notification.title());
        assertEquals(NotificationType.REMINDER, notification.type());
    }

    @Test
    void removeEvent_removesEvent() {
        // Arrange
        Long adminId = 1L;
        Instant now = Instant.now().plusSeconds(3600);
        EventDTO event = new EventDTO(1L, "Test Event", now, false);

        EventManager.getAdminEvents().computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>()).add(event);

        // Act
        eventManager.removeEvent(adminId, event);

        // Assert
        ConcurrentSkipListSet<EventDTO> events = EventManager.getAdminEvents().get(adminId);
        assertTrue(events == null || !events.contains(event));
    }

    @Test
    void updateUrgentStatus_marksEventsAsUrgent() {
        // Arrange
        Long adminId = 1L;
        Instant now = Instant.now();
        EventDTO pastEvent = new EventDTO(1L, "Past Event", now.minusSeconds(3600), false);
        EventDTO futureEvent = new EventDTO(2L, "Future Event", now.plusSeconds(3800), false);

        ConcurrentSkipListSet<EventDTO> events = new ConcurrentSkipListSet<>();
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
