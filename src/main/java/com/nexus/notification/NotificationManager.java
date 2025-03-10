package com.nexus.notification;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
public class NotificationManager {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationManager.class);

    private final Queue<NotificationDTO> notificationQueue = new LinkedList<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final Executor taskExecutor;

    private static final int BATCH_SIZE = 50;

    public NotificationManager(
            SimpMessagingTemplate messagingTemplate,
            NotificationRepository notificationRepository,
            UserService userService,
            @Qualifier("taskExecutor") Executor taskExecutor
    ) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(fixedRate = 3600000)
    public void notificationCleanup() {
        LOG.info("Cleaning up notifications");
        taskExecutor.execute(() -> {
            Instant now = Instant.now();

            List<Notification> notifications = notificationRepository.findAll();
            List<Notification> notificationsToDelete = new ArrayList<>();

            for (Notification notification : notifications) {
                long hoursDifference = ChronoUnit.HOURS.between(notification.getDate(), now);
                if (hoursDifference < 0) {
                    LOG.debug("Removing expired notification: {}", notification.getId());
                    notificationsToDelete.add(notification);
                }
            }

            notificationRepository.deleteAll(notificationsToDelete);
        });
    }

    @Transactional
    public void flush() {
        LOG.info("Starting flush operation.");
        List<CreateNotificationDTO> batch = new ArrayList<>(BATCH_SIZE);

        while (!notificationQueue.isEmpty()) {
            NotificationDTO notification = notificationQueue.poll();
            if (notification != null) {
                LOG.debug("Adding notification to batch: {}", notification);
                batch.add(new CreateNotificationDTO(
                        notification.userId(),
                        notification.title(),
                        notification.body(),
                        notification.type()
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

    public void addNotification(NotificationDTO notification) {
        LOG.debug("Adding single notification to the queue: {}", notification);
        notificationQueue.offer(notification);
    }

    public void addBatchNotification(List<NotificationDTO> notifications) {
        LOG.debug("Adding batch of notifications to the queue. Batch size: {}", notifications.size());
        notificationQueue.addAll(notifications);
    }

    private void processBatch(List<CreateNotificationDTO> createNotificationDTOS) {
        LOG.info("Processing a batch of {} notifications.", createNotificationDTOS.size());
        List<Notification> notifications = saveAll(createNotificationDTOS);

        for (Notification notification : notifications) {
            LOG.debug("Sending notification to user: {}", notification.getUser().getUsername());
            messagingTemplate.convertAndSendToUser(
                    notification.getUser().getUsername(),
                    "user/notification",
                    notification
            );
        }
    }

    private List<Notification> saveAll(List<CreateNotificationDTO> createNotificationDTOS) {
        LOG.info("Saving a batch of {} notifications.", createNotificationDTOS.size());

        Set<Long> userIds = createNotificationDTOS.stream()
                .map(CreateNotificationDTO::userId)
                .collect(Collectors.toSet());
        LOG.debug("Fetching users with IDs: {}", userIds);

        Map<Long, User> usersMap = userService.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<Notification> notifications = new ArrayList<>(createNotificationDTOS.size());
        for (CreateNotificationDTO dto : createNotificationDTOS) {
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
