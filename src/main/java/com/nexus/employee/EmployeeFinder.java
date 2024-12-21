package com.nexus.employee;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class EmployeeFinder {
    private final EmployeeRepository employeeRepository;

    public EmployeeFinder(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("employee with id " + id + " not found"));
    }
}
