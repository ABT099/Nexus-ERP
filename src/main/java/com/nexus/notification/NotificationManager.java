package com.nexus.notification;

import jakarta.transaction.Transactional;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class NotificationManager {

    private final Queue<NotificationHolderDto> notificationQueue = new LinkedList<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    private static final int BATCH_SIZE = 50;

    public NotificationManager(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Transactional
    public void flush() {
        List<CreateNotificationDto> batch = new ArrayList<>(BATCH_SIZE);

        while (!notificationQueue.isEmpty()) {
            NotificationHolderDto notification = notificationQueue.poll();
            if (notification != null) {
                batch.add(new CreateNotificationDto(
                        notification.getUserId(),
                        notification.getTitle(),
                        notification.getBody(),
                        notification.getType()
                ));

                // Process the batch when full
                if (batch.size() >= BATCH_SIZE) {
                    processBatch(batch);
                    batch.clear();
                }
            }
        }

        // Process remaining notifications
        if (!batch.isEmpty()) {
            processBatch(batch);
        }
    }

    public void addNotification(NotificationHolderDto notification) {
        notificationQueue.offer(notification);
    }

    public void addBatchNotification(List<NotificationHolderDto> notifications) {
        notificationQueue.addAll(notifications);
    }

    private void processBatch(List<CreateNotificationDto> createNotificationDtos) {
        List<Notification> notifications = notificationService.saveAll(createNotificationDtos);

        for (Notification notification : notifications) {
            messagingTemplate.convertAndSendToUser(
                    notification.getUser().getUsername(),
                    "user/notification",
                    notification
            );
        }
    }
}
