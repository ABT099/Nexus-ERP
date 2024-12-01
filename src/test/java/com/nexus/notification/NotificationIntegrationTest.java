package com.nexus.notification;

import com.nexus.config.TestContainerConfig;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
public class NotificationIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserCreationContext userContext;

    @Test
    void canReadAllNotifications() {
        UserDto userDto = userContext.create("abdo", "password", UserType.SUPER_USER);
        CreateNotificationDto createNotificationDto = new CreateNotificationDto(userDto.user().getId(), "title", "body", NotificationType.REMINDER);
        notificationService.save(createNotificationDto);

        List<Notification> notificationsResult = webClient.get().uri("/notifications/user/{id}", userDto.user().getId())
                .header("Authorization", "Bearer " + userDto.token())
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
                .header("Authorization", "Bearer " + userDto.token())
                .body(Mono.just(notificationsResult.stream().map(AbstractPersistable::getId).toList()), List.class).exchange().expectStatus().isOk();

        List<Notification> read = webClient.get().uri("/notifications/user/{id}", userDto.user().getId())
                .header("Authorization", "Bearer " + userDto.token())
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
