package com.nexus.notification;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> findAllByUserId(long userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    public void save(CreateNotificationDto createNotificationDto) {
        User user = userRepository.findById(createNotificationDto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Notification notification = new Notification(
                user,
                createNotificationDto.title(),
                createNotificationDto.body(),
                createNotificationDto.date());

        notificationRepository.save(notification);
    }

    public void readBatch(Set<Long> ids) {
        List<Notification> notifications = notificationRepository.findAllById(ids);

        if (notifications.isEmpty()) {
            return;
        }

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    public void delete(long id) {
        notificationRepository.deleteById(id);
    }
}
