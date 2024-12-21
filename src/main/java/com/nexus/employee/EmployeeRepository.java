package com.nexus.employee;

import com.nexus.common.AuthenticatedEntityRepository;

public interface EmployeeRepository extends AuthenticatedEntityRepository<Employee, Long> {
}
