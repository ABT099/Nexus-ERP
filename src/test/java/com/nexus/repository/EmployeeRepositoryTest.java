package com.nexus.repository;

import com.nexus.employee.Employee;
import com.nexus.employee.EmployeeRepository;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private UUID tenantId;
    private Employee employee;

    @BeforeEach
    void setUp() {
        // Create and save a primary user and employee
        Tenant tenant = tenantRepository.save(new Tenant());
        tenantId = tenant.getId();

        user = new User("employee", "password", UserType.EMPLOYEE,tenantId);
        userRepository.save(user);

        employee = new Employee(user, "Abdo", "Towait",  "code");
        employeeRepository.save(employee);
    }

    private void prepareEmployeeList() {
        // Create and save a secondary user and employee
        User secondaryUser = new User("employee2", "password", UserType.EMPLOYEE, tenantId);
        userRepository.save(secondaryUser);

        Employee secondaryEmployee = new Employee(secondaryUser, "FirstName2", "LastName2",  "code2");

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
