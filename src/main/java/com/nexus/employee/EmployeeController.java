package com.nexus.employee;

import com.nexus.common.ArchivableQueryType;
import com.nexus.person.CreatePersonRequest;
import com.nexus.person.UpdatePersonRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            )ArchivableQueryType queryType) {
        List<Employee> employees;

        switch (queryType) {
            case ALL -> employees = employeeService.findAll();
            case Archived -> employees = employeeService.findAllArchived();
            default -> employees = employeeService.findAllNonArchived();
        }

        return ResponseEntity.ok(employees);
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getById(
            @Valid
            @Positive
            @PathVariable("id") long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @GetMapping("me")
    public ResponseEntity<Employee> getMe() {
        return ResponseEntity.ok(employeeService.findMe());
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CreatePersonRequest request) {
        Long id = employeeService.save(request);

        return ResponseEntity.created(URI.create("employees/" + id)).body(id);
    }

    @PutMapping("me")
    public void updateMe(@RequestBody UpdatePersonRequest request) {
        employeeService.updateMe(request);
    }

    @PutMapping("{id}")
    public void update(
            @Valid
            @Positive
            @PathVariable long id, @RequestBody UpdatePersonRequest request) {
        employeeService.updateById(id, request);
    }

    @PatchMapping("archive/{id}")
    public void archive(
            @Valid
            @Positive
            @PathVariable long id) {
        employeeService.archive(id);
    }

    @DeleteMapping("{id}")
    public void delete(
            @Valid
            @Positive
            @PathVariable long id) {
        employeeService.delete(id);
    }
}
