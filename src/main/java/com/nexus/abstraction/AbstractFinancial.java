package com.nexus.abstraction;

import com.nexus.admin.Admin;
import com.nexus.project.Project;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.time.ZonedDateTime;

@MappedSuperclass
public abstract class AbstractFinancial extends AbstractAuditable<Admin, Integer> {
    private double amount;
    private ZonedDateTime paymentDate;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public AbstractFinancial(double amount, ZonedDateTime paymentDate, Project project) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.project = project;
    }

    public AbstractFinancial(double amount, ZonedDateTime paymentDate) {
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

    public ZonedDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(ZonedDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
