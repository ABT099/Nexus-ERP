package com.nexus.notification;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class NotificationManager {

    private final Queue<NotificationHolderDto> notificationQueue = new LinkedList<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    private static final int BATCH_SIZE = 50;

    public NotificationManager(SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepository, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
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
        List<Notification> notifications = saveAll(createNotificationDtos);

        for (Notification notification : notifications) {
            messagingTemplate.convertAndSendToUser(
                    notification.getUser().getUsername(),
                    "user/notification",
                    notification
            );
        }
    }

    private List<Notification> saveAll(List<CreateNotificationDto> createNotificationDtos) {
        Set<Long> userIds = createNotificationDtos.stream()
                .map(CreateNotificationDto::userId)
                .collect(Collectors.toSet());

        Map<Long, User> usersMap = userService.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<Notification> notifications = new ArrayList<>(createNotificationDtos.size());
        for (CreateNotificationDto dto : createNotificationDtos) {
            User user = usersMap.get(dto.userId());
            if (user == null) {
                throw new ResourceNotFoundException("User not found for ID: " + dto.userId());
            }
            notifications.add(new Notification(user, dto.title(), dto.body(), dto.type()));
        }

        return notificationRepository.saveAll(notifications);
    }
}
