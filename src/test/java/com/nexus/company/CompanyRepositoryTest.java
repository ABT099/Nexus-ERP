package com.nexus.company;

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
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Company company;
    private User user;

    @BeforeEach
    void setUp() {
        // Clear existing data in the database
        userRepository.deleteAll();
        companyRepository.deleteAll();

        // Create and save a new user and company
        user = new User("customer", "password", UserType.CUSTOMER);
        userRepository.save(user);

        company = new Company(user, "CompanyName");
        companyRepository.save(company);
    }

    private void prepareCompanyList() {
        // Create additional user and company instances
        User secondaryUser = new User("customer12", "password", UserType.CUSTOMER);
        userRepository.save(secondaryUser);

        Company secondaryCompany = new Company(secondaryUser, "CompanyName1");
        company.setArchived(true); // Archive the primary company

        companyRepository.saveAll(List.of(company, secondaryCompany));
    }

    @Test
    void shouldFindAllNonArchivedCompanies() {
        // Arrange
        prepareCompanyList();

        // Act
        List<Company> nonArchivedCompanies = companyRepository.findAllNonArchived();

        // Assert
        assertEquals(1, nonArchivedCompanies.size());
        assertFalse(nonArchivedCompanies.getFirst().isArchived());
    }

    @Test
    void shouldFindAllArchivedCompanies() {
        // Arrange
        prepareCompanyList();

        // Act
        List<Company> archivedCompanies = companyRepository.findAllArchived();

        // Assert
        assertEquals(1, archivedCompanies.size());
        assertTrue(archivedCompanies.getFirst().isArchived());
    }

    @Test
    void shouldFindCompanyByUserId() {
        // Act
        Optional<Company> foundCompany = companyRepository.findByUserId(user.getId());

        // Assert
        assertTrue(foundCompany.isPresent());
        assertEquals(company.getId(), foundCompany.get().getId());
    }

    @Test
    void shouldArchiveCompanyById() {
        // Act
        companyRepository.archiveById(company.getId());
        entityManager.clear(); // Clear the persistence context to ensure fresh data retrieval

        Optional<Company> archivedCompany = companyRepository.findById(company.getId());

        // Assert
        assertTrue(archivedCompany.isPresent());
        assertTrue(archivedCompany.get().isArchived());
    }

    @Test
    void shouldArchiveUserById() {
        // Act
        companyRepository.archiveUserById(company.getId());
        entityManager.clear(); // Clear the persistence context to ensure fresh data retrieval

        Optional<User> archivedUser = userRepository.findById(company.getUser().getId());

        // Assert
        assertTrue(archivedUser.isPresent());
        assertTrue(archivedUser.get().isArchived());
    }
}
