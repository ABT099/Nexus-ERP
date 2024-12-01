package com.nexus.chat;

import com.github.javafaker.Faker;
import com.nexus.config.TestContainerConfig;
import com.nexus.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.http.MediaType.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
public class MessageIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserCreationContext userCreationContext;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    Faker faker = new Faker();

    @BeforeEach
    public void setup() {
        String username = faker.name().username();
        String password = faker.internet().password();
        UserDto userDto = userCreationContext.create(username, password, UserType.SUPER_USER);

        Chat chat = new Chat();
        chatRepository.save(chat);

        Message message = new Message(
                userDto.user(),
                chat,
                "text"
        );

        messageRepository.save(message);


        assertNotNull(message.getId());
        assertNotNull(chat.getId());
        assertNotNull(userDto.token());

        messageId = message.getId();
        token = userDto.token();
        chatId = chat.getId();
    }

    private Long messageId;
    private Long chatId;
    private String token;

    @Test
    void canUpdateMessage() {
        String newText = "new text";

        UpdateMessageRequest updateRequest = new UpdateMessageRequest(messageId, newText);

        webClient.put()
                .uri("/messages")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(Mono.just(updateRequest), UpdateMessageRequest.class)
                .exchange()
                .expectStatus().isOk();

        webClient.get()
                .uri("/messages/{chatId}", chatId)
                .header("Authorization", "Bearer " + token)
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
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        webClient.get()
                .uri("/messages/{chatId}", chatId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponse.class)
                .value(messages -> assertTrue(messages.stream()
                        .noneMatch(msg -> msg.id().equals(messageId))));
    }
}
