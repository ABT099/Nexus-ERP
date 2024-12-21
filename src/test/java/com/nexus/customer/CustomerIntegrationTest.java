package com.nexus.customer;

import com.github.javafaker.Faker;
import com.nexus.admin.AdminCreationService;
import com.nexus.auth.LoginRequest;
import com.nexus.auth.RegisterResponse;
import com.nexus.common.person.CreatePersonRequest;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
@Transactional
public class CustomerIntegrationTest {
    @Autowired
    private WebTestClient webClient;

    private Faker faker;
    private String adminToken;

    @PostConstruct
    public void init() {
        faker = new Faker();
        AdminCreationService adminCreationService = new AdminCreationService(webClient, faker);
        adminToken = adminCreationService.createAdmin().token();
    }

    @Test
    void canCreateCustomer() {
        RegisterResponse response = createCustomerAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Customer customer = getCustomer(response);

        assertNotNull(customer);
        assertEquals(customer.getId(), response.id());
    }

    @Test
    void canUpdateCustomerById() {
        RegisterResponse response = createCustomerAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Customer customer = getCustomer(response);

        assertNotNull(customer);
        assertEquals(customer.getId(), response.id());

        String newFirstName = faker.name().firstName();
        String newLastName = faker.name().lastName();

        UpdatePersonRequest request = new UpdatePersonRequest(newFirstName, newLastName);

        webClient.put()
                .uri("/customers/" + response.id())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .body(Mono.just(request), UpdatePersonRequest.class)
                .exchange()
                .expectStatus().isOk();

        Customer updatedCustomer = getCustomer(response);

        assertNotNull(updatedCustomer);
        assertEquals(updatedCustomer.getFirstName(), newFirstName);
        assertEquals(updatedCustomer.getLastName(), newLastName);
    }

    @Test
    void canUpdateCustomerWhenMe() {
        RegisterResponse response = createCustomerAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Customer customer = getCustomer(response);
        assertNotNull(customer);
        assertEquals(customer.getId(), response.id());

        String newFirstName = faker.name().firstName();
        String newLastName = faker.name().lastName();

        UpdatePersonRequest request = new UpdatePersonRequest(newFirstName, newLastName);

        webClient.put()
                .uri("/customers/me")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .body(Mono.just(request), UpdatePersonRequest.class)
                .exchange()
                .expectStatus().isOk();

        Customer updatedCustomer = getCustomer(response);

        assertNotNull(updatedCustomer);
        assertEquals(updatedCustomer.getFirstName(), newFirstName);
        assertEquals(updatedCustomer.getLastName(), newLastName);
    }

    @Test
    void canArchiveCustomer() {
        RegisterResponse response = createCustomerAndLogin(adminToken);
        assertNotNull(response);
        assertTrue(response.id() > 0);

        Customer customer = getCustomer(response);
        assertNotNull(customer);
        assertEquals(customer.getId(), response.id());

        webClient.patch()
                .uri("/customers/archive/{id}", customer.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void canDeleteCustomer() {
        RegisterResponse response = createCustomerAndLogin(adminToken);
        assertNotNull(response);
        assertTrue(response.id() > 0);

        Customer customer = getCustomer(response);
        assertNotNull(customer);
        assertEquals(customer.getId(), response.id());

        webClient.delete()
                .uri("/customers/{id}", customer.getId())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk();
    }

    private RegisterResponse createCustomerAndLogin(String token) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String username = faker.name().username();
        CreatePersonRequest request = new CreatePersonRequest(firstName, lastName, username, "password");

        Long id = webClient.post()
                .uri("/customers")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(Mono.just(request), CreatePersonRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<Long>() {})
                .returnResult()
                .getResponseBody();;

        LoginRequest loginRequest = new LoginRequest(username, "password");

        String customerToken = webClient.post()
                .uri("/auth/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchange()
                .expectBody(new ParameterizedTypeReference<String>() {})
                .returnResult()
                .getResponseBody();

        return new RegisterResponse(id , customerToken);
    }

    private Customer getCustomer(RegisterResponse response) {

        return webClient.get()
                .uri("/customers/{id}", response.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();
    }
}
