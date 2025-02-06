package com.nexus.abstraction;

import com.nexus.budget.Budget;
import com.nexus.project.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

@MappedSuperclass
public abstract class AbstractPayment extends AuditableTenantAware<Long> {
    
    @Positive
    private double amount;

    private Instant paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Column(nullable = false)
    private boolean archived = false;

    public AbstractPayment(double amount, Instant paymentDate, Project project) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.project = project;
    }

    public AbstractPayment(double amount, Instant paymentDate) {
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public AbstractPayment() {}

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }
}
