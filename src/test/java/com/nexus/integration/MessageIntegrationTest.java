package com.nexus.integration;

import com.nexus.chat.*;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;

public class MessageIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @BeforeEach
    public void setup() {
        createUser();

        Tenant tenant = new Tenant();
        tenantRepository.save(tenant);

        Chat chat = new Chat(tenant.getId());
        chatRepository.save(chat);

        Message message = new Message(
                user,
                chat,
                "text"
        );

        messageRepository.save(message);

        assertNotNull(message.getId());
        assertNotNull(chat.getId());

        messageId = message.getId();
        chatId = chat.getId();
    }

    private Long messageId;
    private Long chatId;

    @Test
    void canUpdateMessage() {
        String newText = "new text";

        UpdateMessageRequest updateRequest = new UpdateMessageRequest(messageId, newText);

        webTestClient.put()
                .uri("/messages")
                .contentType(APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(updateRequest), UpdateMessageRequest.class)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/messages/{chatId}", chatId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponse.class)
                .value(messages -> assertTrue(messages.stream()
                        .anyMatch(msg -> msg.id().equals(messageId) && msg.text().equals(newText))));
    }

    @Test
    void canDeleteMessage() {
        webTestClient.delete()
                .uri("/messages/{id}", messageId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/messages/{chatId}", chatId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponse.class)
                .value(messages -> assertTrue(messages.stream()
                        .noneMatch(msg -> msg.id().equals(messageId))));
    }
}
