package com.nexus.admin;

import com.nexus.auth.RegisterResponse;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.http.MediaType.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canCreateAdmin() {
        RegisterResponse adminResponse = createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        Admin admin = getAdmin(adminResponse);

        assertNotNull(admin);
        assertEquals(admin.getId(), adminResponse.id());
    }

    @Test
    void canUpdateAdminById() {
        RegisterResponse adminResponse = createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        Admin admin = getAdmin(adminResponse);

        assertNotNull(admin);
        assertEquals(admin.getId(), adminResponse.id());

        UpdatePersonRequest updatePersonRequest = new UpdatePersonRequest("newFirstName", "newLastName");

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
        RegisterResponse adminResponse = createAdmin();

        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        Admin admin = getAdmin(adminResponse);
        assertNotNull(admin);
        assertEquals(admin.getId(), adminResponse.id());

        UpdatePersonRequest updatePersonRequest = new UpdatePersonRequest("newFirstName", "newLastName");

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
        RegisterResponse adminResponse = createAdmin();

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
        RegisterResponse adminResponse = createAdmin();
        assertNotNull(adminResponse);
        assertTrue(adminResponse.id() > 0);

        webTestClient.delete()
                .uri("/admins/{id}", adminResponse.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminResponse.token())
                .exchange()
                .expectStatus().isOk();
    }

    private RegisterResponse createAdmin() {
        CreatePersonRequest createPersonRequest = new CreatePersonRequest("abdo", "towait", "abdo123", "1234a1234");

        return webTestClient.post()
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
