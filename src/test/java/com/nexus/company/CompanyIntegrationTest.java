package com.nexus.company;

import com.github.javafaker.Faker;
import com.nexus.admin.AdminCreationService;
import com.nexus.auth.LoginRequest;
import com.nexus.auth.RegisterResponse;
import jakarta.annotation.PostConstruct;
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
public class CompanyIntegrationTest {

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
    void canCreateCompany() {
        RegisterResponse response = createCompanyAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Company company = getCompany(response);

        assertNotNull(company);
        assertEquals(company.getId(), response.id());
    }

    @Test
    void canUpdateCompanyById() {
        RegisterResponse response = createCompanyAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Company company = getCompany(response);

        assertNotNull(company);
        assertEquals(company.getId(), response.id());

        String newCompanyName = faker.company().name();

        UpdateCompanyRequest request = new UpdateCompanyRequest(company.getId(), newCompanyName);

        webClient.put()
                .uri("/companies")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .body(Mono.just(request), UpdateCompanyRequest.class)
                .exchange()
                .expectStatus().isOk();

        Company updatedCompany = getCompany(response);

        assertNotNull(updatedCompany);
        assertEquals(updatedCompany.getCompanyName(), newCompanyName);
    }

    @Test
    void canUpdateCompanyWhenMe() {
        RegisterResponse response = createCompanyAndLogin(adminToken);

        assertNotNull(response);
        assertTrue(response.id() > 0);

        Company company = getCompany(response);
        assertNotNull(company);
        assertEquals(company.getId(), response.id());

        String newCompanyName = faker.company().name();

        webClient.put()
                .uri("/companies/me")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .body(Mono.just(newCompanyName), String.class)
                .exchange()
                .expectStatus().isOk();

        Company updatedCompany = getCompany(response);

        assertNotNull(updatedCompany);
        assertEquals(updatedCompany.getCompanyName(), newCompanyName);
    }

    @Test
    void canArchiveCompany() {
        RegisterResponse response = createCompanyAndLogin(adminToken);
        assertNotNull(response);
        assertTrue(response.id() > 0);

        Company company = getCompany(response);
        assertNotNull(company);
        assertEquals(company.getId(), response.id());

        webClient.patch()
                .uri("/companies/archive/{id}", company.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void canDeleteCompany() {
        RegisterResponse response = createCompanyAndLogin(adminToken);
        assertNotNull(response);
        assertTrue(response.id() > 0);

        Company company = getCompany(response);
        assertNotNull(company);
        assertEquals(company.getId(), response.id());

        webClient.delete()
                .uri("/companies/{id}", company.getId())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk();
    }

    private RegisterResponse createCompanyAndLogin(String token) {
        String companyName = faker.company().name();
        String username = faker.name().username();
        CreateCompanyRequest request = new CreateCompanyRequest(companyName, username, "password");

         Long id = webClient.post()
                .uri("/companies")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(Mono.just(request), CreateCompanyRequest.class)
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

    private Company getCompany(RegisterResponse response) {

        return webClient.get()
                .uri("/companies/{id}", response.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Company>() {})
                .returnResult()
                .getResponseBody();
    }
}
