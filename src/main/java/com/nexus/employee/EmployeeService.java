package com.nexus.employee;

import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
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

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> getAllNonArchived() {
        return employeeRepository.findAllNonArchived();
    }

    public List<Employee> getAllArchived() {
        return employeeRepository.findAllArchived();
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("employee with id " + id + " not found"));
    }

    public Employee getMe() {
        return getById(getUserId());
    }

    public void create(CreatePersonRequest request) {
        User createdUser = userCreationService.create(request.username(), request.password(), UserType.ADMIN);

        Employee customer = new Employee(createdUser, request.firstName(), request.lastName());

        employeeRepository.save(customer);
    }

    public void updateById(Long id, UpdatePersonRequest request) {
        Employee employee = getById(id);

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    public void updateMe(UpdatePersonRequest request) {
        Employee employee = getMe();

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    public void archive(Long id) {
        employeeRepository.archiveById(id);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
