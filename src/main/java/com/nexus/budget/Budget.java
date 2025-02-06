package com.nexus.budget;

import com.nexus.abstraction.AbstractPayment;
import com.nexus.abstraction.AuditableTenantAware;
import com.nexus.expense.Expense;
import com.nexus.income.Income;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.time.Instant;
import java.util.List;

@Entity
public class Budget extends AuditableTenantAware<Long> {

    @Column(nullable = false, columnDefinition = "text")
    String name;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    Instant startDate;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    Instant endDate;

    @Column(nullable = false)
    double budget;

    double currentTotal;
    double totalIncome;
    double totalExpense;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Income> incomes;
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Expense> expenses;

    @Column(columnDefinition = "text")
    String notice;

    @Column(nullable = false)
    boolean active;

    @Column(nullable = false)
    boolean archived = false;

    public Budget() {}

    public Budget(Instant startDate, Instant endDate, double budget, double currentTotal, boolean active) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.currentTotal = currentTotal;
        this.active = active;
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
}
