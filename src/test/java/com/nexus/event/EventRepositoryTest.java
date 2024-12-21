package com.nexus.event;

import com.nexus.admin.Admin;
import com.nexus.admin.AdminRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByAdminId() {
        // Arrange
        User user = new User("user", "pass", UserType.ADMIN);
        userRepository.save(user);

        Admin admin = new Admin(user, "first", "last");
        adminRepository.save(admin);

        adminRepository.save(admin);

        List<Event> events = List.of(
                new Event("event1", "event1Desc", EventType.MEETING, ZonedDateTime.now().plusDays(1)),
                new Event("event2", "event2Desc", EventType.MEETING, ZonedDateTime.now().plusDays(1))
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
    void updateUrgentToTrue() {
        // Arrange
        List<Event> events = List.of(
                new Event("event1", "event1Desc", EventType.MEETING, ZonedDateTime.now().plusDays(1)),
                new Event("event2", "event2Desc", EventType.MEETING, ZonedDateTime.now().plusDays(1))
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
}
