package com.nexus.projectstep;

import com.nexus.abstraction.AbstractAppAuditing;
import com.nexus.common.Status;
import com.nexus.employee.Employee;
import com.nexus.ineteraction.Interaction;
import com.nexus.project.Project;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class ProjectStep extends AbstractAppAuditing<Integer> {
    @Column(nullable = false, columnDefinition = "text")
    private String name;
    @Column(nullable = false, columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private Instant startDate;
    @Column(nullable = false)
    private Instant expectedEndDate;
    private Instant actualEndDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "step")
    private List<Interaction> interactions;

    @Column(nullable = false)
    private boolean archived = false;

    public ProjectStep(Project project, String name, String description, Instant startDate, Instant expectedEndDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.project = project;
    }

    public ProjectStep() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(Instant expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public Instant getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Instant actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<Interaction> getInteractions() {
        return interactions;
    }
}
