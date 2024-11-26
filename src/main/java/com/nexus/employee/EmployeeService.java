package com.nexus.employee;

import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService extends AbstractUserService {
    private final EmployeeRepository employeeRepository;
    private final UserCreationService userCreationService;
    private final PersonService<Employee> personService;

    public EmployeeService(EmployeeRepository employeeRepository, UserCreationService userCreationService, PersonService<Employee> personService) {
        this.employeeRepository = employeeRepository;
        this.userCreationService = userCreationService;
        this.personService = personService;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findAllNonArchived() {
        return employeeRepository.findAllNonArchived();
    }

    public List<Employee> findAllArchived() {
        return employeeRepository.findAllArchived();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("employee with id " + id + " not found"));
    }

    public Employee findMe() {
        return employeeRepository.findByUserId(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Error with the authenticated user")
                );
    }

    @Transactional
    public void save(CreatePersonRequest request) {
        UserDto userDto = userCreationService.create(request.username(), request.password(), UserType.EMPLOYEE);

        Employee customer = new Employee(userDto.user(), request.firstName(), request.lastName());

        employeeRepository.save(customer);
    }

    public void updateById(Long id, UpdatePersonRequest request) {
        Employee employee = findById(id);

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    public void updateMe(UpdatePersonRequest request) {
        Employee employee = findMe();

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    public void archive(Long id) {
        employeeRepository.archiveById(id);
        employeeRepository.archiveUserById(id);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
