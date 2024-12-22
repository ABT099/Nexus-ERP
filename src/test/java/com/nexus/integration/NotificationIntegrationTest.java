package com.nexus.integration;

import com.nexus.notification.Notification;
import com.nexus.notification.NotificationRepository;
import com.nexus.notification.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void canReadAllNotifications() {
        createUser();

        Notification notification = new Notification(user, "title", "body", NotificationType.REMINDER);

        notificationRepository.save(notification);

        List<Notification> notificationsResult = webClient.get().uri("/notifications/user/{id}", user.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Notification.class)
                .value(notifications -> assertTrue(notifications.stream()
                        .anyMatch(n -> n.getTitle().equals("title") && n.getBody().equals("body"))))
                .returnResult().getResponseBody();

        assertNotNull(notificationsResult);
        assertFalse(notificationsResult.isEmpty());


        webClient.patch()
                .uri("/notifications")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(notificationsResult.stream().map(AbstractPersistable::getId).toList()), List.class).exchange().expectStatus().isOk();

        List<Notification> read = webClient.get().uri("/notifications/user/{id}", user.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Notification.class)
                .value(notifications -> assertTrue(notifications.stream()
                        .anyMatch(n -> n.getTitle().equals("title") && n.getBody().equals("body") && n.isRead())))
                .returnResult().getResponseBody();

        assertNotNull(read);
        assertFalse(read.isEmpty());
    }
}
