package com.nexus.budget;

import com.nexus.abstraction.AbstractPayment;
import com.nexus.abstraction.AuditableTenantAware;
import com.nexus.expense.Expense;
import com.nexus.income.Income;
import com.nexus.project.Project;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
public class Budget extends AuditableTenantAware<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private Instant startDate;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private double budget;

    private double currentTotal;
    private double totalIncome;
    private double totalExpense;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Income> incomes;
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    @Column(columnDefinition = "text")
    private String notice;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean archived;

    public Budget() {}

    public Budget(String name, Instant startDate, Instant endDate, double budget, double currentTotal, boolean active, UUID tenantId) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.currentTotal = currentTotal;
        this.active = active;
        setTenantId(tenantId);
    }

    public <T extends AbstractPayment> void addPayment(T payment) {
        if (payment instanceof Income income) {
            if (budget == currentTotal) {
                notice = "budget exceeded";
            }

            incomes.add(income);
            income.setBudget(this);
            totalIncome += income.getAmount();
            currentTotal += income.getAmount();
        } else if (payment instanceof Expense expense) {
            expenses.add(expense);
            expense.setBudget(this);
            totalExpense += expense.getAmount();
            currentTotal -= expense.getAmount();
        } else {
            throw new IllegalArgumentException("Unsupported payment type: " + payment.getClass().getName());
        }
    }

    public <T extends AbstractPayment> void removePayment(T payment) {
        if (payment instanceof Income income) {
            incomes.remove(income);
            income.setBudget(null);
            totalIncome -= income.getAmount();
            currentTotal -= income.getAmount();
        } else if (payment instanceof Expense expense) {
            expenses.remove(expense);
            expense.setBudget(null);
            totalExpense -= expense.getAmount();
            currentTotal += expense.getAmount();
        } else {
            throw new IllegalArgumentException("Unsupported payment type: " + payment.getClass().getName());
        }
    }

    public double getIncomePercentage() {
        return totalIncome / currentTotal * 100;
    }

    public double getExpensePercentage() {
        return totalExpense / currentTotal * 100;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getCurrentTotal() {
        return currentTotal;
    }

    public void setCurrentTotal(double total) {
        this.currentTotal = total;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public String getNotice() {
        return notice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archive) {
        this.archived = archive;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
