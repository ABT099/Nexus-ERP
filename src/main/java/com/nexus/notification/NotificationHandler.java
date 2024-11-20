package com.nexus.notification;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class NotificationHandler {

    private final Queue<NotificationHolderDto> notifcationQueue = new LinkedList<>();

    private final NotificationService notificationService;

    public NotificationHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @Transactional
    public void flush() {
        List<CreateNotificationDto> createNotificationDtos = notifcationQueue.stream()
                .map(n -> new CreateNotificationDto(n.getUserId(), n.getTitle(), n.getBody(), n.getType()))
                .toList();

        notificationService.saveAll(createNotificationDtos);
    }

    public void addNotification(NotificationHolderDto notification) {
        notifcationQueue.add(notification);
    }

    public void addBatchNotification(List<NotificationHolderDto> notifications) {
        notifcationQueue.addAll(notifications);
    }
}
