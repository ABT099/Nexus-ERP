package com.nexus.expense;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexus.abstraction.AbstractFinancial;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.project.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

import java.time.ZonedDateTime;

@Entity
public class Expense extends AbstractFinancial {
    @ManyToOne(optional = false)
    private ExpenseCategory expenseCategory;

    public Expense(double amount, ZonedDateTime paymentDate, Project project, ExpenseCategory expenseCategory) {
        super(amount, paymentDate, project);
        this.expenseCategory = expenseCategory;
    }

    public Expense(double amount, ZonedDateTime paymentDate, ExpenseCategory expenseCategory) {
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
