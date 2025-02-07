package com.nexus.repository;

import com.nexus.admin.Admin;
import com.nexus.admin.AdminRepository;
import com.nexus.event.Event;
import com.nexus.event.EventRepository;
import com.nexus.event.EventType;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class EventRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByAdminId() {
        // Arrange
        Admin admin = prepareAdmin();

        List<Event> events = List.of(
                new Event("event1", "event1Desc", EventType.MEETING, Instant.now().plus(1, ChronoUnit.DAYS)),
                new Event("event2", "event2Desc", EventType.MEETING,  Instant.now().plus(1, ChronoUnit.DAYS))
        );

        for (Event event : events) {
            event.addAdmin(admin);
        }

        eventRepository.saveAll(events);

        // Act
        List<Event> actual = eventRepository.findAllByAdminId(admin.getId());

        // Assert
        assertEquals(actual.size(), events.size());
        assertIterableEquals(actual, admin.getEvents());
    }

    @Test
    void shouldFindAllNonArchivedByAdminId() {
        Admin admin = prepareAdmin();

        List<Event> events = List.of(
                new Event("event1", "event1Desc", EventType.MEETING, Instant.now().plus(1, ChronoUnit.DAYS)),
                new Event("event2", "event2Desc", EventType.MEETING,  Instant.now().plus(1, ChronoUnit.DAYS))
        );

        for (Event event : events) {
            event.addAdmin(admin);
        }

        events.getFirst().setArchived(true);
        eventRepository.saveAll(events);

        List<Event> actual = eventRepository.findAllNonArchivedByAdminId(admin.getId());

        assertEquals(actual.size(), events.size() - 1);

        for (Event event : actual) {
            assertFalse(event.isArchived());
        }
    }

    @Test
    void updateUrgentToTrue() {
        // Arrange
        List<Event> events = List.of(
                new Event("event1", "event1Desc", EventType.MEETING,  Instant.now().plus(1, ChronoUnit.DAYS)),
                new Event("event2", "event2Desc", EventType.MEETING,  Instant.now().plus(1, ChronoUnit.DAYS))
        );

        eventRepository.saveAll(events);

        // Act
        eventRepository.updateUrgentToTrue(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        entityManager.clear();

        List<Event> updated = eventRepository.findAll();

        assertEquals(updated.size(), events.size());

        for (Event event : updated) {
            assertTrue(event.isUrgent());
        }
    }

    private Admin prepareAdmin() {
        Tenant tenant = tenantRepository.save(new Tenant());

        User user = new User("user", "pass", UserType.ADMIN, tenant.getId());
        userRepository.save(user);

        Admin admin = new Admin(user, "first", "last");
        adminRepository.save(admin);

        adminRepository.save(admin);

        return admin;
    }
}
