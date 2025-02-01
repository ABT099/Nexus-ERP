package com.nexus.integration;

import com.github.javafaker.Faker;
import com.nexus.auth.LoginRequest;
import com.nexus.config.TestContainerConfig;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserCreationContext userCreationContext;

    Faker faker = new Faker();

    @Test
    void canLogin() {
        String username = faker.name().username();
        String password = faker.internet().password();

        // make sure to use this service because it hashes the password otherwise it will always return bad credentials
        userCreationContext.create(username, password, UserType.SUPER_USER);

        LoginRequest loginRequest = new LoginRequest(username, password);

        String token = webClient.post()
                .uri("auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<String>() {})
                .returnResult().getResponseBody();

        assertNotNull(token);
    }
}
