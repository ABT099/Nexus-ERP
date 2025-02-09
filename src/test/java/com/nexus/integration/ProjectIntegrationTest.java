package com.nexus.integration;

import com.nexus.common.Status;
import com.nexus.project.CreateProjectRequest;
import com.nexus.project.ProjectResponse;
import com.nexus.project.UpdateProjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProjectIntegrationTest extends AuthenticatedIntegrationTest {

    private Integer id;

    @BeforeEach
    public void setup() {
        createUser();
    }

    @Test
    void canCreateProject() {
        createProject();
        getProject();
    }

    @Test
    void canUpdateProject() {
        createProject();
        ProjectResponse project = getProject();

        UpdateProjectRequest request = new UpdateProjectRequest(
                project.id(),
                "New Project Name",
                "New Project Description",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                Instant.now().plus(2, ChronoUnit.DAYS),
                12345
        );

        webTestClient.put()
                .uri("/projects")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UpdateProjectRequest.class)
                .exchange()
                .expectStatus().isOk();

        ProjectResponse updatedProject = getProject();

        assertNotNull(updatedProject);

        assertNotEquals(project.name(), updatedProject.name());
        assertNotEquals(project.description(), updatedProject.description());
    }

    @Test
    void canUpdateStatus() {
        createProject();
        ProjectResponse project = getProject();

        webTestClient.patch()
                .uri("/projects/{id}/status", id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Status.CANCELLED), Status.class)
                .exchange()
                .expectStatus().isOk();

        ProjectResponse updatedProject = getProject();

        assertNotNull(updatedProject);

        assertNotEquals(project.status(), updatedProject.status());
    }

    @Test
    void canDeleteProject() {
        createProject();
        getProject();

        webTestClient.delete()
                .uri("/projects/{id}", id)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/projects/{id}", id)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void canArchiveProject() {
        createProject();
        getProject();

        webTestClient.patch()
                .uri("/projects/archive/{id}", id)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk();

        ProjectResponse archivedProject = getProject();

        assertNotNull(archivedProject);
    }

    private void createProject() {
        CreateProjectRequest request = new CreateProjectRequest(
                user.getId(),
                "Project Name",
                "Project Description",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                12344
        );

        id = webTestClient.post()
                .uri("/projects")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateProjectRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Integer.class)
                .returnResult().getResponseBody();

        assertNotNull(id);
    }

    private ProjectResponse getProject() {
        ProjectResponse project = webTestClient.get()
                .uri("/projects/{id}", id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectResponse.class).returnResult().getResponseBody();

        assertNotNull(project);

        return project;
    }
}
