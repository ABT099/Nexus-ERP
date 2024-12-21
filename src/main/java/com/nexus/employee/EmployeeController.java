package com.nexus.employee;

import com.nexus.abstraction.UserContext;
import com.nexus.common.ArchivableQueryType;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("employees")
public class EmployeeController extends UserContext {

    private final EmployeeRepository employeeRepository;
    private final UserCreationContext userCreationContext;
    private final PersonService<Employee> personService;
    private final EmployeeFinder employeeFinder;

    public EmployeeController(EmployeeRepository employeeRepository, UserCreationContext userCreationContext, PersonService<Employee> personService, EmployeeFinder employeeFinder) {
        this.employeeRepository = employeeRepository;
        this.userCreationContext = userCreationContext;
        this.personService = personService;
        this.employeeFinder = employeeFinder;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            )ArchivableQueryType queryType) {
        List<Employee> employees;

        switch (queryType) {
            case ALL -> employees = employeeRepository.findAll();
            case Archived -> employees = employeeRepository.findAllArchived();
            default -> employees = employeeRepository.findAllNonArchived();
        }

        return ResponseEntity.ok(employees);
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getById(@Valid @Positive @PathVariable long id) {
        return ResponseEntity.ok(employeeFinder.findById(id));
    }

    @GetMapping("me")
    public ResponseEntity<Employee> getMe() {
        return ResponseEntity.ok(findFromAuth());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Long> create(@Valid @RequestBody CreatePersonRequest request) {
        UserDto userDto = userCreationContext.create(request.username(), request.password(), UserType.EMPLOYEE);

        Employee employee = new Employee(userDto.user(), request.firstName(), request.lastName());

        employeeRepository.save(employee);


        return ResponseEntity.created(URI.create("employees/" + employee.getId())).body(employee.getId());
    }

    @PutMapping("me")
    public void updateMe(@RequestBody UpdatePersonRequest request) {
        Employee employee = findFromAuth();

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable long id, @RequestBody UpdatePersonRequest request) {
        Employee employee = employeeFinder.findById(id);

        employee = personService.updatePerson(employee, request);

        employeeRepository.save(employee);
    }

    @PatchMapping("archive/{id}")
    @Transactional
    public void archive(@Valid @Positive @PathVariable long id) {
        employeeRepository.archiveById(id);
        employeeRepository.archiveUserById(id);
    }

    @DeleteMapping("{id}")
    public void delete(
            @Valid
            @Positive
            @PathVariable long id) {
        employeeRepository.deleteById(id);
    }

    private Employee findFromAuth() {
        return employeeRepository.findByUserId(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Error with the authenticated user")
                );
    }
}
