package com.nexus.integration;

import com.nexus.income.CreateIncomeRequest;
import com.nexus.income.IncomeResponse;
import com.nexus.project.CreateProjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IncomeIntegrationTest extends AuthenticatedIntegrationTest {

    private long incomeId;

    @BeforeEach
    public void setup() {
        createUser();
    }

    @Test
    void canCreateIncomeWithProject() {
        CreateProjectRequest projectRequest = new CreateProjectRequest(
                user.getId(),
                "project name",
                "project description",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                100.0
        );

        Integer projectId = webTestClient.post()
                .uri("/projects")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(projectRequest), CreateProjectRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Integer.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(projectId);

        createIncome(projectId);

        IncomeResponse income = webTestClient.get()
                .uri("/incomes/" + incomeId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IncomeResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(income);
        assertNotNull(income.project());
    }

    @Test
    void canUpdateIncome() {
        createIncome();

        webTestClient.put()
                .uri("/incomes/" + incomeId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new CreateIncomeRequest(200.0, Instant.now(), null, user.getId())), CreateIncomeRequest.class)
                .exchange()
                .expectStatus().isOk();

        IncomeResponse income = webTestClient.get()
                .uri("/incomes/" + incomeId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IncomeResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(income);
    }

    @Test
    void canDeleteIncome() {
        createIncome();

        webTestClient.delete()
                .uri("/incomes/" + incomeId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/incomes/" + incomeId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void canArchiveIncome() {
        createIncome();

        webTestClient.patch()
                .uri("/incomes/archive/" + incomeId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();


        webTestClient.get()
                .uri("/incomes/" + incomeId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    private void createIncome(Integer... projectId) {
        CreateIncomeRequest request = new CreateIncomeRequest(
                100.0,
                Instant.now(),
                projectId.length > 0 ? projectId[0] : null,
                user.getId()
        );

        Long id = webTestClient.post()
                .uri("/incomes")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateIncomeRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(id);

        incomeId = id;
    }
}
