package com.nexus.project;

import com.nexus.abstraction.AbstractWorkItem;
import com.nexus.employee.Employee;
import com.nexus.expense.Expense;
import com.nexus.file.File;
import com.nexus.payment.Payment;
import com.nexus.projectstep.ProjectStep;
import com.nexus.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Project extends AbstractWorkItem {
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
    private List<Payment> payments = new ArrayList<>();

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

    @Positive
    private double price;

    public Project(User owner, double price, String name, String description, ZonedDateTime startDate, ZonedDateTime expectedEndDate) {
        super(name, description, startDate, expectedEndDate);
        this.owner = owner;
        this.price = price;
    }

    public Project() {}

    public User getOwner() {
        return owner;
    }

    public List<ProjectStep> getSteps() {
        return steps;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public List<Expense> getExpenses() {
        return expenses;
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

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
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
}
