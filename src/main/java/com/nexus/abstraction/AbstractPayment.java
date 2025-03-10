package com.nexus.abstraction;

import com.nexus.budget.Budget;
import com.nexus.project.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

@MappedSuperclass
public abstract class AbstractPayment extends AuditableTenantAware<Long> {
    
    @Positive
    private long amount;

    private Instant paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Column(nullable = false)
    private boolean archived = false;

    @Column(nullable = false, columnDefinition = "text")
    private String currency;

    public AbstractPayment(long amount, Instant paymentDate, Project project, String currency) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.project = project;
        this.currency = currency;
    }

    public AbstractPayment(long amount, Instant paymentDate, String currency) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.currency = currency;
    }

    public AbstractPayment() {}

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
