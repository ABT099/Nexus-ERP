package com.nexus.employee;

import com.nexus.common.person.PersonRepository;

public interface EmployeeRepository extends PersonRepository<Employee, Long> {
}
