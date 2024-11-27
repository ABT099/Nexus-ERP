package com.nexus.auth;

import com.github.javafaker.Faker;
import com.nexus.user.UserService;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    Faker faker = new Faker();

    @Test
    void canLogin() {
        userRepository.deleteAll();

        String username = faker.name().username();
        String password = faker.internet().password();

        // make sure to use this service because it hashes the password otherwise it will always return bad credentials
        userService.create(username, password, UserType.SUPER_USER);

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
