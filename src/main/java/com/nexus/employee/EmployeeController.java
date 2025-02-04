package com.nexus.employee;

import com.nexus.abstraction.UserContext;
import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDTO;
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
    private final EmployeeMapper employeeMapper;

    public EmployeeController(EmployeeRepository employeeRepository, UserCreationContext userCreationContext, PersonService<Employee> personService, EmployeeFinder employeeFinder, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.userCreationContext = userCreationContext;
        this.personService = personService;
        this.employeeFinder = employeeFinder;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping
    public ResponseEntity<List<BasicEmployeeResponse>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            )ArchivableQueryType queryType) {
        List<Employee> employees = ArchivedService.determine(queryType, employeeRepository);

        return ResponseEntity.ok(employees.stream().map(employeeMapper::toBasicEmployeeResponse).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeResponse> getById(@Valid @Positive @PathVariable long id) {
        Employee employee = employeeFinder.findById(id);

        return ResponseEntity.ok(employeeMapper.toEmployeeResponse(employee));
    }

    @GetMapping("me")
    public ResponseEntity<EmployeeResponse> getMe() {
        Employee employee = findFromAuth();

        return ResponseEntity.ok(employeeMapper.toEmployeeResponse(employee));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Long> create(@Valid @RequestBody CreatePersonRequest request) {
        UserDTO userDto = userCreationContext.create(request.username(), request.password(), UserType.EMPLOYEE);

        // Todo: Generate emp code..
        String employeeCode = "";

        Employee employee = new Employee(userDto.user(), request.firstName(), request.lastName(), employeeCode);

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
    public void delete(@Valid @Positive @PathVariable long id) {
        employeeRepository.deleteById(id);
    }

    private Employee findFromAuth() {
        return employeeRepository.findByUserId(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Error with the authenticated user")
                );
    }
}
