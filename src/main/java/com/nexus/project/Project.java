package com.nexus.project;

import com.nexus.abstraction.AuditableTenantAware;
import com.nexus.budget.Budget;
import com.nexus.common.Status;
import com.nexus.employee.Employee;
import com.nexus.expense.Expense;
import com.nexus.file.File;
import com.nexus.ineteraction.Interaction;
import com.nexus.income.Income;
import com.nexus.projectstep.ProjectStep;
import com.nexus.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.*;

@Entity
public class Project extends AuditableTenantAware<Integer> {

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant expectedEndDate;

    private Instant actualEndDate;

    @Positive
    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProjectStep> steps = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_files",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<File> files = new HashSet<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Budget> budgets = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Income> incomes = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_employees",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    private List<Interaction> interactions;

    @Column(nullable = false)
    private boolean archived = false;

    public Project(User owner, double price, String name, String description, Instant startDate, Instant expectedEndDate, UUID tenantId) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.owner = owner;
        this.price = price;
        setTenantId(tenantId);
    }

    public Project() {}

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

    public User getOwner() {
        return owner;
    }

    public List<ProjectStep> getSteps() {
        return steps;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Budget> getBudgets() {
        return budgets;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public Set<File> getFiles() {
        return files;
    }

    public void addFile(File file) {
        files.add(file);
        file.getProjects().add(this);
    }

    public void removeFile(File file) {
        files.remove(file);
        file.getProjects().remove(this);
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
