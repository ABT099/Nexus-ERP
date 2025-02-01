package com.nexus.abstraction;

import com.nexus.project.Project;
import jakarta.persistence.*;

import java.time.Instant;

@MappedSuperclass
public abstract class AbstractFinancial extends AuditableTenantAware<Integer> {
    private double amount;
    private Instant paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public AbstractFinancial(double amount, Instant paymentDate, Project project) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.project = project;
    }

    public AbstractFinancial(double amount, Instant paymentDate) {
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public AbstractFinancial() {}

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
}
