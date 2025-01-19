package com.nexus.integration;

import com.github.javafaker.Faker;
import com.nexus.admin.AdminResponse;
import com.nexus.admin.BasicAdminResponse;
import com.nexus.unit.AdminCreationService;
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


import java.util.List;

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

        AdminResponse admin = getAdmin(adminResponse);

        assertNotNull(admin);
        assertEquals(admin.id(), adminResponse.id());
    }

    @Test
    void canUpdateAdminById() {
        RegisterResponse adminResponse = adminCreationService.createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        AdminResponse admin = getAdmin(adminResponse);

        assertNotNull(admin);
        assertEquals(admin.id(), adminResponse.id());

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

        AdminResponse admin2 = getAdmin(adminResponse);

        assertNotNull(admin2);
        assertNotEquals(admin.firstName(), admin2.firstName());
        assertNotEquals(admin.lastName(), admin2.lastName());
    }

    @Test
    void canUpdateAdminWhenMe() {
        RegisterResponse adminResponse = adminCreationService.createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        AdminResponse admin = getAdmin(adminResponse);
        assertNotNull(admin);
        assertEquals(admin.id(), adminResponse.id());

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

        AdminResponse admin2 = getAdmin(adminResponse);

        assertNotNull(admin2);
        assertNotEquals(admin.firstName(), admin2.firstName());
        assertNotEquals(admin.lastName(), admin2.lastName());
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

        List<BasicAdminResponse> admins = webTestClient.get()
                .uri("/admins?a=Archived")
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<BasicAdminResponse>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(admins);
        assertFalse(admins.isEmpty());
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

    private AdminResponse getAdmin(RegisterResponse adminResponse) {

        return webTestClient.get()
                .uri("/admins/{id}", adminResponse.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<AdminResponse>() {})
                .returnResult()
                .getResponseBody();
    }
}
