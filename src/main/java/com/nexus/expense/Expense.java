package com.nexus.expense;

import com.nexus.abstraction.AbstractPayment;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.project.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Expense extends AbstractPayment {
    @ManyToOne(optional = false)
    private ExpenseCategory expenseCategory;

    public Expense(long amount, String currency, Instant paymentDate, Project project, ExpenseCategory expenseCategory, UUID tenantId) {
        super(amount, paymentDate, project, currency);
        this.expenseCategory = expenseCategory;
        setTenantId(tenantId);
    }

    public Expense(long amount, String currency, Instant paymentDate, ExpenseCategory expenseCategory, UUID tenantId) {
        super(amount, paymentDate, currency);
        this.expenseCategory = expenseCategory;
        setTenantId(tenantId);
    }

    public Expense() {}

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }
}
