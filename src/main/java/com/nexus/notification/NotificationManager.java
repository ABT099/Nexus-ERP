package com.nexus.notification;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class NotificationManager {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationManager.class);

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
        LOG.info("Starting flush operation.");
        List<CreateNotificationDto> batch = new ArrayList<>(BATCH_SIZE);

        while (!notificationQueue.isEmpty()) {
            NotificationHolderDto notification = notificationQueue.poll();
            if (notification != null) {
                LOG.debug("Adding notification to batch: {}", notification);
                batch.add(new CreateNotificationDto(
                        notification.getUserId(),
                        notification.getTitle(),
                        notification.getBody(),
                        notification.getType()
                ));

                // Process the batch when full
                if (batch.size() >= BATCH_SIZE) {
                    LOG.info("Processing batch of size: {}", batch.size());
                    processBatch(batch);
                    batch.clear();
                }
            }
        }

        // Process remaining notifications
        if (!batch.isEmpty()) {
            LOG.info("Processing remaining batch of size: {}", batch.size());
            processBatch(batch);
        }

        LOG.info("Flush operation completed.");
    }

    public void addNotification(NotificationHolderDto notification) {
        LOG.debug("Adding single notification to the queue: {}", notification);
        notificationQueue.offer(notification);
    }

    public void addBatchNotification(List<NotificationHolderDto> notifications) {
        LOG.debug("Adding batch of notifications to the queue. Batch size: {}", notifications.size());
        notificationQueue.addAll(notifications);
    }

    private void processBatch(List<CreateNotificationDto> createNotificationDtos) {
        LOG.info("Processing a batch of {} notifications.", createNotificationDtos.size());
        List<Notification> notifications = saveAll(createNotificationDtos);

        for (Notification notification : notifications) {
            LOG.debug("Sending notification to user: {}", notification.getUser().getUsername());
            messagingTemplate.convertAndSendToUser(
                    notification.getUser().getUsername(),
                    "user/notification",
                    notification
            );
        }
    }

    private List<Notification> saveAll(List<CreateNotificationDto> createNotificationDtos) {
        LOG.info("Saving a batch of {} notifications.", createNotificationDtos.size());

        Set<Long> userIds = createNotificationDtos.stream()
                .map(CreateNotificationDto::userId)
                .collect(Collectors.toSet());
        LOG.debug("Fetching users with IDs: {}", userIds);

        Map<Long, User> usersMap = userService.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<Notification> notifications = new ArrayList<>(createNotificationDtos.size());
        for (CreateNotificationDto dto : createNotificationDtos) {
            User user = usersMap.get(dto.userId());
            if (user == null) {
                LOG.error("User not found for ID: {}", dto.userId());
                throw new ResourceNotFoundException("User not found for ID: " + dto.userId());
            }
            LOG.debug("Creating notification for user: {}", user.getUsername());
            notifications.add(new Notification(user, dto.title(), dto.body(), dto.type()));
        }

        return notificationRepository.saveAll(notifications);
    }
}
