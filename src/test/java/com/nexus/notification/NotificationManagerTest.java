package com.nexus.notification;

import com.nexus.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationManagerTest {
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private NotificationManager notificationManager;

    @Test
    void flush_sendsNotificationsAndClearsQueue() {
        NotificationHolderDto notificationDto = new NotificationHolderDto(1L, "Test Title", "Test Body", NotificationType.REMINDER);
        Notification notification = new Notification(new User(), "Test Title", "Test Body", NotificationType.REMINDER);

        when(notificationService.saveAll(anyList())).thenReturn(List.of(notification));

        notificationManager.addNotification(notificationDto);
        notificationManager.flush();

        verify(notificationService).saveAll(anyList());
        verify(messagingTemplate).convertAndSendToUser(
                eq(notification.getUser().getUsername()),
                eq("user/notification"),
                eq(notification)
        );

        Queue<NotificationHolderDto> queue = extractQueue(notificationManager);
        assertTrue(queue.isEmpty());
    }

    @Test
    void flush_doesNothing_whenQueueIsEmpty() {
        notificationManager.flush();

        verifyNoInteractions(notificationService);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void flush_processesBatchNotifications() {
        String title = "Test Title";
        String body = "Test Body";

        NotificationHolderDto dto1 = new NotificationHolderDto(1L, title, body, NotificationType.REMINDER);
        NotificationHolderDto dto2 = new NotificationHolderDto(2L, title, body, NotificationType.REMINDER);

        Notification notification1 = new Notification(new User(), title, body, NotificationType.REMINDER);
        Notification notification2 = new Notification(new User(), title, body, NotificationType.REMINDER);

        when(notificationService.saveAll(anyList())).thenReturn(List.of(notification1, notification2));

        notificationManager.addBatchNotification(List.of(dto1, dto2));
        notificationManager.flush();

        @SuppressWarnings("unchecked") // Suppress unchecked warning for raw type
        ArgumentCaptor<List<CreateNotificationDto>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationService).saveAll(captor.capture());

        List<CreateNotificationDto> savedDtos = captor.getValue();
        assertEquals(2, savedDtos.size());
        assertEquals(title, savedDtos.get(0).title());
        assertEquals(title, savedDtos.get(1).title());

        verify(messagingTemplate).convertAndSendToUser(
                eq(notification1.getUser().getUsername()),
                eq("user/notification"),
                eq(notification1)
        );
        verify(messagingTemplate).convertAndSendToUser(
                eq(notification2.getUser().getUsername()),
                eq("user/notification"),
                eq(notification2)
        );

        Queue<NotificationHolderDto> queue = extractQueue(notificationManager);
        assertTrue(queue.isEmpty());
    }

    @SuppressWarnings("unchecked")
    private Queue<NotificationHolderDto> extractQueue(NotificationManager manager) {
        // Reflection is used to access the private `notificationQueue` field.
        try {
            var field = NotificationManager.class.getDeclaredField("notificationQueue");
            field.setAccessible(true);
            return (Queue<NotificationHolderDto>) field.get(manager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to extract queue", e);
        }
    }
}
