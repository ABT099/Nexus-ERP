package com.nexus.projectstep;

import com.nexus.abstraction.AbstractWorkItem;
import com.nexus.employee.Employee;
import com.nexus.project.Project;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ProjectStep extends AbstractWorkItem {
    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "step_employees",
            joinColumns = @JoinColumn(name = "step_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees = new HashSet<>();

    public ProjectStep(Project project, String name, String description, ZonedDateTime startDate, ZonedDateTime expectedEndDate) {
        super(name, description, startDate, expectedEndDate);
        this.project = project;
    }

    public ProjectStep() {
    }

    public Project getProject() {
        return project;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }
}
