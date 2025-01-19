package com.nexus.integration;

import com.nexus.notification.Notification;
import com.nexus.notification.NotificationRepository;
import com.nexus.notification.NotificationResponse;
import com.nexus.notification.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void canReadAllNotifications() {
        createUser();

        Notification notification = new Notification(user, "title", "body", NotificationType.REMINDER);

        notificationRepository.save(notification);

        List<NotificationResponse> notificationsResult = webTestClient.get().uri("/notifications/user/{id}", user.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificationResponse.class)
                .value(notifications -> assertTrue(notifications.stream()
                        .anyMatch(n -> n.title().equals("title") && n.body().equals("body"))))
                .returnResult().getResponseBody();

        assertNotNull(notificationsResult);
        assertFalse(notificationsResult.isEmpty());


        webTestClient.patch()
                .uri("/notifications")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(notificationsResult.stream().map(NotificationResponse::id).toList()), List.class).exchange().expectStatus().isOk();

        List<NotificationResponse> read = webTestClient.get().uri("/notifications/user/{id}", user.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificationResponse.class)
                .value(notifications -> assertTrue(notifications.stream()
                        .anyMatch(n -> n.title().equals("title") && n.body().equals("body") && n.isRead())))
                .returnResult().getResponseBody();

        assertNotNull(read);
        assertFalse(read.isEmpty());
    }
}
