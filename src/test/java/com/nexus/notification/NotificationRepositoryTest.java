package com.nexus.notification;


import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllNotificationsByUserId() {
        // Arrange
        User user = new User("username", "password", UserType.SUPER_USER);

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
