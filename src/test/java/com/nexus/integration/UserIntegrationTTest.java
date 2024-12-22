package com.nexus.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class UserIntegrationTTest extends AuthenticatedIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @BeforeEach
    public void setUp() {
        createUser();
    }

    @Test
    void canChangeUsername() {
        webClient.patch()
                .uri("/users/username")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just("newUsername"), String.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void canChangePassword() {
        webClient.patch()
                .uri("/users/password")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just("newPassword"), String.class)
                .exchange()
                .expectStatus().isOk();
    }
}
