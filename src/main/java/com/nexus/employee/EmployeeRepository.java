package com.nexus.employee;

import com.nexus.person.PersonRepository;

public interface EmployeeRepository extends PersonRepository<Employee, Long> {
}
