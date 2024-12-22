package com.nexus.integration;

import com.nexus.chat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;

public class MessageIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @BeforeEach
    public void setup() {
        createUser();

        Chat chat = new Chat();
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

        webClient.put()
                .uri("/messages")
                .contentType(APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(updateRequest), UpdateMessageRequest.class)
                .exchange()
                .expectStatus().isOk();

        webClient.get()
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
        webClient.delete()
                .uri("/messages/{id}", messageId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk();

        webClient.get()
                .uri("/messages/{chatId}", chatId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponse.class)
                .value(messages -> assertTrue(messages.stream()
                        .noneMatch(msg -> msg.id().equals(messageId))));
    }
}
