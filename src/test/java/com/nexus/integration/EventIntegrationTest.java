package com.nexus.integration;

import com.github.javafaker.Faker;
import com.nexus.auth.RegisterResponse;
import com.nexus.common.Status;
import com.nexus.abstraction.AbstractAppUser;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.config.TestContainerConfig;
import com.nexus.event.CreateEventRequest;
import com.nexus.event.Event;
import com.nexus.event.EventType;
import com.nexus.event.UpdateEventRequest;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
public class EventIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserCreationContext userContext;

    Faker faker = new Faker();

    private int eventId;
    private String token;

    @BeforeEach
    public void setup() {
        String username = faker.name().username();
        String password = faker.internet().password();


        UserDto userDto = userContext.create(username, password, UserType.SUPER_USER);

        CreateEventRequest request = new CreateEventRequest(Set.of(userDto.user().getId()), "event name", "event description", EventType.MEETING, ZonedDateTime.now().plusDays(2));

        Integer id = webClient.post()
                .uri("/events")
                .header("Authorization", "Bearer " + userDto.token())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateEventRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Integer.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(id);
        assertTrue(id > 0);

        eventId = id;
        token = userDto.token();
    }

    @Test
    void canUpdateEvent() {
        Event event = getEvent();

        assertNotNull(event);

        UpdateEventRequest request = new UpdateEventRequest("newName", "newDescription", EventType.MEETING, Status.PENDING, ZonedDateTime.now().plusDays(2));

        webClient.put()
                .uri("/events/{id}", eventId)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UpdateEventRequest.class)
                .exchange()
                .expectStatus().isOk();

        Event newEvent = getEvent();

        assertNotNull(newEvent);

        assertNotEquals(newEvent.getName(), event.getName());
        assertNotEquals(newEvent.getDescription(), event.getName());
        assertNotEquals(newEvent.getDate(), event.getDate());
    }

    @Test
    void canDeleteEvent() {
        webClient.delete()
                .uri("/events/{id}", eventId)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

       webClient.get()
                .uri("/events/{id}", eventId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void canAddAdmin() {
        Long adminId = createAdmin();

        webClient.patch()
                .uri("/events/{eventId}/add-admin/{adminId}", eventId, adminId)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        Event event = getEvent();

        assertNotNull(event);

        List<Long> ids = event.getAdmins().stream().map(AbstractAppUser::getId).toList();
        assertTrue(ids.contains(adminId));
    }

    @Test
    void canRemoveAdmin() {
        Long adminId = createAdmin();

        webClient.patch()
                .uri("/events/{eventId}/remove-admin/{adminId}", eventId, adminId)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        Event event = getEvent();

        assertNotNull(event);

        List<Long> ids = event.getAdmins().stream().map(AbstractAppUser::getId).toList();
        assertFalse(ids.contains(adminId));
    }

    private Event getEvent() {

        return webClient.get()
                .uri("/events/{id}", eventId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Event.class)
                .returnResult().getResponseBody();
    }


    private Long createAdmin() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String username = faker.name().username();
        String password = faker.internet().password();

        CreatePersonRequest request = new CreatePersonRequest(firstName, lastName, username, password);
        RegisterResponse response =  webClient.post()
                .uri("/admins")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreatePersonRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertNotNull(response.id());
        assertTrue(response.id() > 0);

        token = response.token();

        return response.id();
    }
}
