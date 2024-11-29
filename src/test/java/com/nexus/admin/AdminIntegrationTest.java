package com.nexus.admin;

import com.github.javafaker.Faker;
import com.nexus.auth.RegisterResponse;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.config.TestContainerConfig;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.http.MediaType.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
@Transactional
public class AdminIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private Faker faker;
    private AdminCreationService adminCreationService;

    @PostConstruct
    public void init() {
        faker = new Faker();
        adminCreationService = new AdminCreationService(webTestClient, faker);
    }

    @Test
    void canCreateAdmin() {
        RegisterResponse adminResponse = adminCreationService.createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        Admin admin = getAdmin(adminResponse);

        assertNotNull(admin);
        assertEquals(admin.getId(), adminResponse.id());
    }

    @Test
    void canUpdateAdminById() {
        RegisterResponse adminResponse = adminCreationService.createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        Admin admin = getAdmin(adminResponse);

        assertNotNull(admin);
        assertEquals(admin.getId(), adminResponse.id());

        String newFirstName = faker.name().firstName();
        String newLastName = faker.name().lastName();
        UpdatePersonRequest updatePersonRequest = new UpdatePersonRequest(newFirstName, newLastName);

        webTestClient.put()
                .uri("/admins/{id}", adminResponse.id())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .body(Mono.just(updatePersonRequest), UpdatePersonRequest.class)
                .exchange()
                .expectStatus().isOk();

        Admin admin2 = getAdmin(adminResponse);

        assertNotNull(admin2);
        assertNotEquals(admin.getFirstName(), admin2.getFirstName());
        assertNotEquals(admin.getLastName(), admin2.getLastName());
    }

    @Test
    void canUpdateAdminWhenMe() {
        RegisterResponse adminResponse = adminCreationService.createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        Admin admin = getAdmin(adminResponse);
        assertNotNull(admin);
        assertEquals(admin.getId(), adminResponse.id());

        String newFirstName = faker.name().firstName();
        String newLastName = faker.name().lastName();
        UpdatePersonRequest updatePersonRequest = new UpdatePersonRequest(newFirstName, newLastName);

        webTestClient.put()
                .uri("/admins/me")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .body(Mono.just(updatePersonRequest), UpdatePersonRequest.class)
                .exchange()
                .expectStatus().isOk();

        Admin admin2 = getAdmin(adminResponse);

        assertNotNull(admin2);
        assertNotEquals(admin.getFirstName(), admin2.getFirstName());
        assertNotEquals(admin.getLastName(), admin2.getLastName());
    }

    @Test
    void canArchiveAdmin() {
        RegisterResponse adminResponse = adminCreationService.createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        webTestClient.patch()
                .uri("/admins/archive/{id}", adminResponse.id())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .exchange()
                .expectStatus().isOk();

        Admin admin = getAdmin(adminResponse);

        assertNotNull(admin);
        assertTrue(admin.isArchived());
    }

    @Test
    void canDeleteAdmin() {
        RegisterResponse adminResponse = adminCreationService.createAdmin();
        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        webTestClient.delete()
                .uri("/admins/{id}", adminResponse.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .exchange()
                .expectStatus().isOk();
    }

    private Admin getAdmin(RegisterResponse adminResponse) {

        return webTestClient.get()
                .uri("/admins/{id}", adminResponse.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Admin>() {})
                .returnResult()
                .getResponseBody();
    }
}
