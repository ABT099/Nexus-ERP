package com.nexus.integration;

import com.nexus.common.Status;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.project.CreateProjectRequest;
import com.nexus.projectstep.CreateProjectStepRequest;
import com.nexus.projectstep.StepResponse;
import com.nexus.projectstep.UpdateProjectStepRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectStepIntegrationTest extends AuthenticatedIntegrationTest {

    private Integer stepId;

    @BeforeEach
    public void setup() {
        createUser();
    }

    @Test
    void canUpdateStep() {
        createStep();
        StepResponse step = getStep();

        UpdateProjectStepRequest request = new UpdateProjectStepRequest(
                step.id(),
                "New Step Name",
                "New Step Description",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                Instant.now().plus(2, ChronoUnit.DAYS)
        );

        webTestClient.put()
                .uri("/steps")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UpdateProjectStepRequest.class)
                .exchange()
                .expectStatus().isOk();

        StepResponse updatedStep = getStep();

        assertNotNull(updatedStep);

        assertNotEquals(step.name(), updatedStep.name());
        assertNotEquals(step.description(), updatedStep.description());
    }

    @Test
    void canDeleteStep() {
        createStep();

        webTestClient.delete()
                .uri("/steps/{id}", stepId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/steps/{id}", stepId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void canUpdateStatus() {
        createStep();

        webTestClient.patch()
                .uri("/steps/{id}/status", stepId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Status.CANCELLED), Status.class)
                .exchange()
                .expectStatus().isOk();

        StepResponse updatedStep = getStep();

        assertNotNull(updatedStep);
        assertEquals(Status.CANCELLED, updatedStep.status());
    }

    @Test
    void canArchive() {
        createStep();

        webTestClient.patch()
                .uri("/steps/archive/{id}", stepId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        StepResponse updatedStep = getStep();

        assertNotNull(updatedStep);
    }

    @Test
    void canAddEmployee() {
        createStep();

        Long empId = createEmployee();

        webTestClient.patch()
                .uri("/steps/{id}/employees/{employeeId}", stepId, empId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        StepResponse updatedStep = getStep();

        assertNotNull(updatedStep);

        assertTrue(updatedStep.employees().stream().anyMatch(e -> e.id().equals(empId)));
    }

    @Test
    void canRemoveEmployee() {
        createStep();

        Long empId = createEmployee();

        webTestClient.delete()
                .uri("/steps/{id}/employees/{employeeId}", stepId, empId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        StepResponse updatedStep = getStep();

        assertNotNull(updatedStep);

        assertFalse(updatedStep.employees().stream().anyMatch(e -> e.id().equals(empId)));
    }

    private void createStep() {
        CreateProjectRequest projectRequest = new CreateProjectRequest(
                user.getId(),
                "Project Name",
                "Project Description",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                12345
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
                .returnResult().getResponseBody();

        CreateProjectStepRequest request = new CreateProjectStepRequest(
                projectId,
                "Step Name",
                "Step Description",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS)
        );

        stepId = webTestClient.post()
                .uri("/steps")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateProjectStepRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Integer.class)
                .returnResult().getResponseBody();
    }

    private StepResponse getStep() {
        return webTestClient.get()
                .uri("/steps/{id}", stepId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StepResponse.class)
                .returnResult().getResponseBody();
    }

    private Long createEmployee() {


        CreatePersonRequest request = new CreatePersonRequest(
                "First Name",
                "Last Name",
                faker.name().username(),
                "af@!AFF213");

        return webTestClient.post()
                .uri("/employees")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreatePersonRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .returnResult().getResponseBody();
    }
}
