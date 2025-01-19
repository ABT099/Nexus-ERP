package com.nexus.employee;

import com.nexus.project.ProjectMapper;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    private final ProjectMapper projectMapper;

    public EmployeeMapper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public BasicEmployeeResponse toBasicEmployeeResponse(Employee employee) {
        return new BasicEmployeeResponse(
                employee.getId(),
                employee.getUser().getAvatarUrl(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmployeeCode()
        );
    }

    public EmployeeResponse toEmployeeResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getUser().getId(),
                employee.getUser().getAvatarUrl(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmployeeCode(),
                employee.getAssignedProjects().stream().map(projectMapper::toBasicProjectResponse).toList()
        );
    }
}
