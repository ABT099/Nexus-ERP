package com.nexus.chat;

import com.github.javafaker.Faker;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.http.MediaType.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessageIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        messageRepository.deleteAll();

        User user = new User("abdo", "te123455", UserType.SUPER_USER);
        userRepository.save(user);

        Chat chat = new Chat("chat");
        chatRepository.save(chat);

        MessageRequest request = new MessageRequest(user.getId(), chat.getId(), "Hello World");
        messageService.sendAndSave(request);
    }

    @Test
    void canUpdateMessage() {
        UpdateMessageRequest updateRequest = new UpdateMessageRequest(1, "newText");

        webClient.put()
                .uri("/messages")
                .contentType(APPLICATION_JSON)
                .body(Mono.just(updateRequest), UpdateMessageRequest.class)
                .exchange()
                .expectStatus().isOk();

        webClient.get()
                .uri("/messages/{chatId}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponse.class)
                .value(messages -> assertTrue(messages.stream()
                        .anyMatch(msg -> msg.id().equals(1L) && msg.text().equals("newText"))));
    }

    @Test
    void canDeleteMessage() {

    }
}
