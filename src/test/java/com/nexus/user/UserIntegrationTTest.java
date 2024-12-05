package com.nexus.user;

import com.nexus.config.TestContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
public class UserIntegrationTTest {
    private String token;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserCreationContext creationContext;

    @BeforeEach
    public void setUp() {
        token = creationContext.create("user", "password", UserType.SUPER_USER).token();
    }

    @Test
    void canChangeUsername() {
        webClient.patch()
                .uri("/users/username")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just("newUsername"), String.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void canChangePassword() {
        webClient.patch()
                .uri("/users/password")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just("newPassword"), String.class)
                .exchange()
                .expectStatus().isOk();
    }
}
