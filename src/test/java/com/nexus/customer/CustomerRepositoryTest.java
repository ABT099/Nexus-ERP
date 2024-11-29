package com.nexus.customer;

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
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Create and save a new user and customer
        user = new User("customer", "password", UserType.CUSTOMER);
        userRepository.save(user);

        customer = new Customer(user, "Abdo", "Towait");
        customerRepository.save(customer);
    }

    private void prepareCustomerList() {
        // Create and save a secondary user and customer
        User secondaryUser = new User("customer2", "password", UserType.CUSTOMER);
        userRepository.save(secondaryUser);

        Customer secondaryCustomer = new Customer(secondaryUser, "FirstName2", "LastName2");

        // Archive the primary customer for testing
        customer.setArchived(true);

        // Save both customers to the repository
        customerRepository.saveAll(List.of(customer, secondaryCustomer));
    }

    @Test
    void shouldFindAllNonArchivedCustomers() {
        // Arrange
        prepareCustomerList();

        // Act
        List<Customer> nonArchivedCustomers = customerRepository.findAllNonArchived();

        // Assert
        assertEquals(1, nonArchivedCustomers.size());
        assertFalse(nonArchivedCustomers.getFirst().isArchived());
    }

    @Test
    void shouldFindAllArchivedCustomers() {
        // Arrange
        prepareCustomerList();

        // Act
        List<Customer> archivedCustomers = customerRepository.findAllArchived();

        // Assert
        assertEquals(1, archivedCustomers.size());
        assertTrue(archivedCustomers.getFirst().isArchived());
    }


    @Test
    void shouldFindCustomerByUserId() {
        // Act
        Optional<Customer> foundCustomer = customerRepository.findByUserId(user.getId());

        // Assert
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getId(), foundCustomer.get().getId());
    }

    @Test
    void shouldArchiveCustomerById() {
        // Act
        customerRepository.archiveById(customer.getId());
        entityManager.clear(); // Clear persistence context to fetch fresh data

        Optional<Customer> archivedCustomer = customerRepository.findById(customer.getId());

        // Assert
        assertTrue(archivedCustomer.isPresent());
        assertTrue(archivedCustomer.get().isArchived());
    }

    @Test
    void shouldArchiveUserById() {
        // Act
        customerRepository.archiveUserById(customer.getId());
        entityManager.clear(); // Clear persistence context to fetch fresh data

        Optional<User> archivedUser = userRepository.findById(customer.getUser().getId());

        // Assert
        assertTrue(archivedUser.isPresent());
        assertTrue(archivedUser.get().isArchived());
    }
}
