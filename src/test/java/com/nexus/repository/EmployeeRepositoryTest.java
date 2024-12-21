package com.nexus.repository;

import com.nexus.employee.Employee;
import com.nexus.employee.EmployeeRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
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
@Transactional
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Employee employee;

    @BeforeEach
    void setUp() {
        // Create and save a primary user and employee
        user = new User("employee", "password", UserType.EMPLOYEE);
        userRepository.save(user);

        employee = new Employee(user, "Abdo", "Towait");
        employeeRepository.save(employee);
    }

    private void prepareEmployeeList() {
        // Create and save a secondary user and employee
        User secondaryUser = new User("employee2", "password", UserType.EMPLOYEE);
        userRepository.save(secondaryUser);

        Employee secondaryEmployee = new Employee(secondaryUser, "FirstName2", "LastName2");

        // Archive the primary employee for testing
        employee.setArchived(true);

        // Save both employees to the repository
        employeeRepository.saveAll(List.of(employee, secondaryEmployee));
    }

    @Test
    void shouldFindEmployeeByUserId() {
        // Act
        Optional<Employee> foundEmployee = employeeRepository.findByUserId(user.getId());

        // Assert
        assertTrue(foundEmployee.isPresent());
        assertEquals(employee.getId(), foundEmployee.get().getId());
    }

    @Test
    void shouldArchiveEmployeeById() {
        // Act
        employeeRepository.archiveById(employee.getId());
        entityManager.clear(); // Clear persistence context to fetch fresh data

        Optional<Employee> archivedEmployee = employeeRepository.findById(employee.getId());

        // Assert
        assertTrue(archivedEmployee.isPresent());
        assertTrue(archivedEmployee.get().isArchived());
    }

    @Test
    void shouldArchiveUserById() {
        // Act
        employeeRepository.archiveUserById(employee.getId());
        entityManager.clear(); // Clear persistence context to fetch fresh data

        Optional<User> archivedUser = userRepository.findById(employee.getUser().getId());

        // Assert
        assertTrue(archivedUser.isPresent());
        assertTrue(archivedUser.get().isArchived());
    }

    @Test
    void shouldFindAllNonArchivedEmployees() {
        // Arrange
        prepareEmployeeList();

        // Act
        List<Employee> nonArchivedEmployees = employeeRepository.findAllNonArchived();

        // Assert
        assertEquals(1, nonArchivedEmployees.size());
        assertFalse(nonArchivedEmployees.getFirst().isArchived());
    }

    @Test
    void shouldFindAllArchivedEmployees() {
        // Arrange
        prepareEmployeeList();

        // Act
        List<Employee> archivedEmployees = employeeRepository.findAllArchived();

        // Assert
        assertEquals(1, archivedEmployees.size());
        assertTrue(archivedEmployees.getFirst().isArchived());
    }

    @Test
    void shouldFindEmployeeById() {
        // Act
        Optional<Employee> foundEmployee = employeeRepository.findById(employee.getId());

        // Assert
        assertTrue(foundEmployee.isPresent());
        assertEquals(employee.getId(), foundEmployee.get().getId());
        assertEquals("Abdo", foundEmployee.get().getFirstName());
        assertEquals("Towait", foundEmployee.get().getLastName());
    }
}
