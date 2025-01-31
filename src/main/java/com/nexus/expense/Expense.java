package com.nexus.expense;

import com.nexus.abstraction.AbstractFinancial;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.project.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.time.Instant;

@Entity
public class Expense extends AbstractFinancial {
    @ManyToOne(optional = false)
    private ExpenseCategory expenseCategory;

    public Expense(double amount, Instant paymentDate, Project project, ExpenseCategory expenseCategory) {
        super(amount, paymentDate, project);
        this.expenseCategory = expenseCategory;
    }

    public Expense(double amount, Instant paymentDate, ExpenseCategory expenseCategory) {
        super(amount, paymentDate);
        this.expenseCategory = expenseCategory;
    }

    public Expense() {}

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }
}
