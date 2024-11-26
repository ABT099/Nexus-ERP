package com.nexus.notification;

import jakarta.transaction.Transactional;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class NotificationManager {

    private final Queue<NotificationHolderDto> notifcationQueue = new LinkedList<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public NotificationManager(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Async
    @Transactional
    public void flush() {
        List<CreateNotificationDto> createNotificationDtos = notifcationQueue.stream()
                .map(n -> new CreateNotificationDto(n.getUserId(), n.getTitle(), n.getBody(), n.getType()))
                .toList();

        if (!createNotificationDtos.isEmpty()) {
            List<Notification> notifications = notificationService.saveAll(createNotificationDtos);
            notifcationQueue.clear();

            for (Notification notification : notifications) {
                messagingTemplate.convertAndSendToUser(
                        notification.getUser().getUsername(),
                        "user/notification",
                        notification
                );
            }
        }
    }

    public void addNotification(NotificationHolderDto notification) {
        notifcationQueue.add(notification);
    }

    public void addBatchNotification(List<NotificationHolderDto> notifications) {
        notifcationQueue.addAll(notifications);
    }
}
