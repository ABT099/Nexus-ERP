package com.nexus.customer;

import com.nexus.AbstractAuthMockTest;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest extends AbstractAuthMockTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserCreationService userCreationService;
    @Mock
    private PersonService<Customer> personService;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void findAll_shouldReturnAllCustomers() {
        // Arrange
        List<Customer> customers = List.of(new Customer());

        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.findAll();

        // Assert
        assertEquals(customers.size(), result.size());
        verify(customerRepository).findAll();
    }

    @Test
    void findAllNonArchived_shouldReturnAllNonArchivedCustomers() {
        // Arrange
        List<Customer> customers = List.of(new Customer());

        when(customerRepository.findAllNonArchived()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.findAllNonArchived();

        // Assert
        assertEquals(customers.size(), result.size());
        verify(customerRepository).findAllNonArchived();
    }

    @Test
    void findAllArchived_shouldReturnAllArchivedCustomers() {
        // Arrange
        List<Customer> customers = List.of(new Customer());

        when(customerRepository.findAllArchived()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.findAllArchived();

        // Assert
        assertEquals(customers.size(), result.size());
        verify(customerRepository).findAllArchived();
    }

    @Test
    void findById_shouldReturnCustomerById() {
        // Arrange
        Customer customer = new Customer();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.findById(1L);

        // Assert
        assertEquals(customer, result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenCustomerDoesNotExist() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.findById(1L));
        verify(customerRepository).findById(1L);
    }

    @Test
    void findMe_shouldReturnCustomer_whenAuthenticated() {
        // Arrange
        Long id = 1L;

        setUpSecurityContext(id);

        Customer customer = new Customer();
        when(customerRepository.findByUserId(id)).thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.findMe();

        // Assert
        assertEquals(customer, result);
        verify(customerRepository).findByUserId(id);
    }


    @Test
    void findMe_shouldThrowException_whenIsNotAuthenticated() {
        // Arrange
        Long id = 1L;
        setUpSecurityContext(id);

        when(customerRepository.findByUserId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.findMe());
        verify(customerRepository).findByUserId(id);
    }

    @Test
    void save() {
        CreatePersonRequest request = new CreatePersonRequest("username", "password", "firstName", "lastName");
        User user = new User();
        when(userCreationService.create(request.username(), request.password(), UserType.CUSTOMER)).thenReturn(user);

        customerService.save(request);

        verify(userCreationService).create(request.username(), request.password(), UserType.CUSTOMER);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateById() {
        Customer customer = new Customer();
        UpdatePersonRequest request = new UpdatePersonRequest("newFirstName", "newLastName");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(personService.updatePerson(customer, request)).thenReturn(customer);

        customerService.updateById(1L, request);

        verify(customerRepository).findById(1L);
        verify(personService).updatePerson(customer, request);
        verify(customerRepository).save(customer);
    }

    @Test
    void archive() {
        customerService.archive(1L);

        verify(customerRepository).archiveById(1L);
        verify(customerRepository).archiveUserById(1L);
    }

    @Test
    void delete() {
        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
    }
}
