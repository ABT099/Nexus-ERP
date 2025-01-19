package com.nexus.employee;

public record BasicEmployeeResponse(
    Long id,
    String avatarUrl,
    String firstName,
    String lastName,
    String employeeCode
) { }
