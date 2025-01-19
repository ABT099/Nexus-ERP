package com.nexus.employee;

import com.nexus.project.BasicProjectResponse;

import java.util.List;

public record EmployeeResponse(
        Long id,
        Long userId,
        String avatarUrl,
        String firstName,
        String lastName,
        String employeeCode,
        List<BasicProjectResponse> assignedProjects
) { }
