package com.nexus.admin;

import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User adminUser;
    private Admin admin;

    @BeforeEach
    void setUp() {
        // Clean the database before each test
        userRepository.deleteAll();
        adminRepository.deleteAll();

        // Create and save a new user and admin
        adminUser = new User("admin", "password", UserType.ADMIN);
        userRepository.save(adminUser);

        admin = new Admin(adminUser, "Abdo", "Towait");
        adminRepository.save(admin);
    }

    private void prepareAdminList() {
        // Additional admin and user setup
        User secondaryUser = new User("admin2", "password", UserType.ADMIN);
        userRepository.save(secondaryUser);

        Admin secondaryAdmin = new Admin(secondaryUser, "FirstName1", "LastName1");
        admin.setArchived(true); // Archive the initial admin for testing

        adminRepository.saveAll(List.of(admin, secondaryAdmin));
    }

    @Test
    void shouldFindAllNonArchivedAdmins() {
        // Arrange
        prepareAdminList();

        // Act
        List<Admin> nonArchivedAdmins = adminRepository.findAllNonArchived();

        // Assert
        assertEquals(1, nonArchivedAdmins.size());
        assertFalse(nonArchivedAdmins.getFirst().isArchived());
    }

    @Test
    void shouldFindAllArchivedAdmins() {
        // Arrange
        prepareAdminList();

        // Act
        List<Admin> archivedAdmins = adminRepository.findAllArchived();

        // Assert
        assertEquals(1, archivedAdmins.size());
        assertTrue(archivedAdmins.getFirst().isArchived());
    }

    @Test
    void shouldFindAdminByUserId() {
        // Act
        Optional<Admin> foundAdmin = adminRepository.findByUserId(adminUser.getId());

        // Assert
        assertTrue(foundAdmin.isPresent());
        assertEquals(admin.getId(), foundAdmin.get().getId());
    }

    @Test
    void shouldArchiveAdminById() {
        // Act
        adminRepository.archiveById(admin.getId());
        entityManager.clear(); // Clear the persistence context to ensure data is fetched from the database

        Optional<Admin> archivedAdmin = adminRepository.findById(admin.getId());

        // Assert
        assertTrue(archivedAdmin.isPresent());
        assertTrue(archivedAdmin.get().isArchived());
    }

    @Test
    void shouldArchiveUserById() {
        // Act
        adminRepository.archiveUserById(admin.getId());
        entityManager.clear(); // Clear the persistence context to ensure data is fetched from the database

        Optional<User> archivedUser = userRepository.findById(admin.getUser().getId());

        // Assert
        assertTrue(archivedUser.isPresent());
        assertTrue(archivedUser.get().isArchived());
    }
}
