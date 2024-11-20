package com.nexus.employee;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            case ALL -> employees = employeeService.getAll();
            case Archived -> employees = employeeService.getAllArchived();
            default -> employees = employeeService.getAllNonArchived();
        }

        return ResponseEntity.ok(employees);
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getById(
            @Valid
            @Positive
            @PathVariable("id") long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @GetMapping("me")
    public ResponseEntity<Employee> getMe() {
        return ResponseEntity.ok(employeeService.getMe());
    }

    @PostMapping
    public void create(@Valid @RequestBody CreatePersonRequest request) {
        employeeService.create(request);
    }

    @PutMapping
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
