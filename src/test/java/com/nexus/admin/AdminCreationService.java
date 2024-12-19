package com.nexus.admin;

import com.github.javafaker.Faker;
import com.nexus.auth.RegisterResponse;
import com.nexus.person.CreatePersonRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class AdminCreationService {

    private final WebTestClient webClient;

    private final Faker faker;

    public AdminCreationService(WebTestClient webClient, Faker faker) {
        this.webClient = webClient;
        this.faker = faker;
    }

    public RegisterResponse createAdmin() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String userName = faker.name().username();
        String password = "123124rawer";

        CreatePersonRequest createPersonRequest = new CreatePersonRequest(firstName, lastName, userName, password);

        return webClient.post()
                .uri("/admins")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(createPersonRequest), CreatePersonRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterResponse.class)
                .returnResult()
                .getResponseBody();
    }
}
