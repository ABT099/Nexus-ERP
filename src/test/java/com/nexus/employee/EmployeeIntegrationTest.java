package com.nexus.employee;

import com.github.javafaker.Faker;
import com.nexus.admin.AdminCreationService;
import com.nexus.auth.LoginRequest;
import com.nexus.auth.RegisterResponse;
import com.nexus.person.CreatePersonRequest;
import com.nexus.person.UpdatePersonRequest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
@Transactional
public class EmployeeIntegrationTest {

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
    void canCreateEmployee() {
        RegisterResponse response = createEmployeeAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Employee employee = getEmployee(response);

        assertNotNull(employee);
        assertEquals(employee.getId(), response.id());
    }

    @Test
    void canUpdateEmployeeById() {
        RegisterResponse response = createEmployeeAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Employee employee = getEmployee(response);

        assertNotNull(employee);
        assertEquals(employee.getId(), response.id());

        String newFirstName = faker.name().firstName();
        String newLastName = faker.name().lastName();

        UpdatePersonRequest request = new UpdatePersonRequest(newFirstName, newLastName);

        webClient.put()
                .uri("/employees/" + response.id())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .body(Mono.just(request), UpdatePersonRequest.class)
                .exchange()
                .expectStatus().isOk();

        Employee updatedEmployee = getEmployee(response);

        assertNotNull(updatedEmployee);
        assertEquals(updatedEmployee.getFirstName(), newFirstName);
        assertEquals(updatedEmployee.getLastName(), newLastName);
    }

    @Test
    void canUpdateEmployeeWhenMe() {
        RegisterResponse response = createEmployeeAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Employee employee = getEmployee(response);
        assertNotNull(employee);
        assertEquals(employee.getId(), response.id());

        String newFirstName = faker.name().firstName();
        String newLastName = faker.name().lastName();

        UpdatePersonRequest request = new UpdatePersonRequest(newFirstName, newLastName);

        webClient.put()
                .uri("/employees/me")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .body(Mono.just(request), UpdatePersonRequest.class)
                .exchange()
                .expectStatus().isOk();

        Employee updatedEmployee = getEmployee(response);

        assertNotNull(updatedEmployee);
        assertEquals(updatedEmployee.getFirstName(), newFirstName);
        assertEquals(updatedEmployee.getLastName(), newLastName);
    }

    @Test
    void canArchiveEmployee() {
        RegisterResponse response = createEmployeeAndLogin(adminToken);
        assertNotNull(response);
        assertTrue(response.id() > 0);

        Employee employee = getEmployee(response);
        assertNotNull(employee);
        assertEquals(employee.getId(), response.id());

        webClient.patch()
                .uri("/employees/archive/{id}", employee.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void canDeleteEmployee() {
        RegisterResponse response = createEmployeeAndLogin(adminToken);
        assertNotNull(response);
        assertTrue(response.id() > 0);

        Employee employee = getEmployee(response);
        assertNotNull(employee);
        assertEquals(employee.getId(), response.id());

        webClient.delete()
                .uri("/employees/{id}", employee.getId())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk();
    }

    private RegisterResponse createEmployeeAndLogin(String token) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String username = faker.name().username();
        CreatePersonRequest request = new CreatePersonRequest(firstName, lastName, username, "password");

        Long id = webClient.post()
                .uri("/employees")
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

    private Employee getEmployee(RegisterResponse response) {

        return webClient.get()
                .uri("/employees/{id}", response.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Employee>() {})
                .returnResult()
                .getResponseBody();
    }
}
