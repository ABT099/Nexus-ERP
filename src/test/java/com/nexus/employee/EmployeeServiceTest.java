package com.nexus.employee;

import com.nexus.AbstractAuthMockTest;
import com.nexus.admin.Admin;
import com.nexus.admin.AdminService;
import com.nexus.person.CreatePersonRequest;
import com.nexus.person.PersonService;
import com.nexus.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest extends AbstractAuthMockTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private UserCreationContext userCreationContext;
    @Mock
    private AdminService adminService;

    @Mock
    private PersonService<Employee> personService;

    @InjectMocks
    private EmployeeService employeeService;


    @Test
    void findAll_shouldReturnAllEmployees() {
        List<Employee> employees = List.of(new Employee(), new Employee());

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.findAll();

        assertEquals(employees.size(), result.size());
        verify(employeeRepository).findAll();
    }

    @Test
    void findAllNonArchived_shouldReturnNonArchivedEmployees() {
        List<Employee> employees = List.of(new Employee(), new Employee());

        when(employeeRepository.findAllNonArchived()).thenReturn(employees);

        List<Employee> result = employeeService.findAllNonArchived();

        assertEquals(employees.size(), result.size());
        verify(employeeRepository).findAllNonArchived();
    }

    @Test
    void findAllArchived_shouldReturnArchivedEmployees() {
        List<Employee> employees = List.of(new Employee(), new Employee());

        when(employeeRepository.findAllArchived()).thenReturn(employees);

        List<Employee> result = employeeService.findAllArchived();

        assertEquals(employees.size(), result.size());
        verify(employeeRepository).findAllArchived();
    }

    @Test
    void findById_shouldReturnEmployee() {
        Employee employee = new Employee();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.findById(1L);

        assertEquals(employee, result);
        verify(employeeRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.findById(1L));

        verify(employeeRepository).findById(1L);
    }

    @Test
    void findMe_shouldReturnEmployee_whenAuthenticated() {
        Long id = 1L;
        setUpSecurityContext(id);

        Employee employee = new Employee();
        when(employeeRepository.findByUserId(id)).thenReturn(Optional.of(employee));

        Employee result = employeeService.findMe();

        assertEquals(employee, result);
        verify(employeeRepository).findByUserId(id);
    }

    @Test
    void findMe_shouldThrowException_whenIsNotAuthenticated() {
        // Arrange
        Long id = 1L;
        setUpSecurityContext(id);

        when(employeeRepository.findByUserId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> employeeService.findMe());
        verify(employeeRepository).findByUserId(id);
    }

    @Test
    void save() {
        CreatePersonRequest request = new CreatePersonRequest("firstName", "password", "firstName123", "lastName");
        UserDto userDto = new UserDto(new User(), "token");
        when(userCreationContext.create(request.username(), request.password(), UserType.EMPLOYEE)).thenReturn(userDto);
        when(adminService.findMe()).thenReturn(new Admin());

        employeeService.save(request);

        verify(userCreationContext).create(request.username(), request.password(), UserType.EMPLOYEE);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateById() {
        Employee employee = new Employee();
        UpdatePersonRequest request = new UpdatePersonRequest("newFirstName", "newLastName");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(personService.updatePerson(employee, request)).thenReturn(employee);

        employeeService.updateById(1L, request);

        verify(employeeRepository).findById(1L);
        verify(personService).updatePerson(employee, request);
        verify(employeeRepository).save(employee);
    }

    @Test
    void archive() {
        employeeService.archive(1L);

        verify(employeeRepository).archiveById(1L);
        verify(employeeRepository).archiveUserById(1L);
    }

    @Test
    void delete() {
        employeeService.delete(1L);

        verify(employeeRepository).deleteById(1L);
    }
}
