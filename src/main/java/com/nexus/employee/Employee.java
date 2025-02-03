package com.nexus.employee;

import com.nexus.abstraction.AbstractPerson;
import com.nexus.project.Project;
import com.nexus.projectstep.ProjectStep;
import com.nexus.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class Employee extends AbstractPerson {

    @Column(
            columnDefinition = "text",
            nullable = false
    )
    private String employeeCode;

    @ManyToMany(
            mappedBy = "employees",
            fetch = FetchType.LAZY
    )
    private List<Project> assignedProjects;

    @ManyToMany(
            mappedBy = "employees",
            fetch = FetchType.LAZY
    )
    private List<ProjectStep> assignedSteps;

    public Employee(User user, String firstName, String lastName, String employeeCode) {
        super(user, firstName, lastName);
        this.employeeCode = employeeCode;
    }

    public Employee() {}

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public List<Project> getAssignedProjects() {
        return assignedProjects;
    }

    public List<ProjectStep> getAssignedSteps() {
        return assignedSteps;
    }
}
