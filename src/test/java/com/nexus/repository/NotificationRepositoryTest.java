package com.nexus.repository;


import com.nexus.notification.Notification;
import com.nexus.notification.NotificationRepository;
import com.nexus.notification.NotificationType;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void findAllNotificationsByUserId() {
        // Arrange
        Tenant tenant = tenantRepository.save(new Tenant());
        User user = new User("username", "password", UserType.SUPER_USER, tenant.getId());

        userRepository.save(user);

        List<Notification> notifications = List.of(
                new Notification(user, "title", "body", NotificationType.REMINDER),
                new Notification(user, "title2", "body2", NotificationType.NEW_UPDATE)
        );

        notificationRepository.saveAll(notifications);

        // Act
        List<Notification> actual = notificationRepository.findAllByUserId(user.getId());

        // Assert
        assertIterableEquals(notifications, actual);
    }
}
