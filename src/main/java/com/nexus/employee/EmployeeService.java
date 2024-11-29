package com.nexus.employee;

import com.nexus.admin.AdminService;
import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserService;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService extends AbstractUserService {
    private final EmployeeRepository employeeRepository;
    private final UserCreationContext userCreationContext;
    private final PersonService<Employee> personService;
    private final AdminService adminService;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            UserCreationContext userCreationContext,
            PersonService<Employee> personService,
            AdminService adminService) {
        this.employeeRepository = employeeRepository;
        this.userCreationContext = userCreationContext;
        this.personService = personService;
        this.adminService = adminService;
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
    public Long save(CreatePersonRequest request) {
        adminService.findMe();

        UserDto userDto = userCreationContext.create(request.username(), request.password(), UserType.EMPLOYEE);

        Employee employee = new Employee(userDto.user(), request.firstName(), request.lastName());

        employeeRepository.save(employee);

        return employee.getId();
    }

    @Transactional
    public void updateById(Long id, UpdatePersonRequest request) {
        Employee employee = findById(id);

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    @Transactional
    public void updateMe(UpdatePersonRequest request) {
        Employee employee = findMe();

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    @Transactional
    public void archive(Long id) {
        employeeRepository.archiveById(id);
        employeeRepository.archiveUserById(id);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
