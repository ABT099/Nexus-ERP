package com.nexus.expensecategory;

import com.nexus.expense.Expense;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ExpenseCategory extends AbstractPersistable<Long> {

    @Column(
            nullable = false,
            columnDefinition = "text"
    )
    private String name;

    @Column(
            nullable = false,
            columnDefinition = "text"
    )
    private String description;

    @OneToMany(
            mappedBy = "expenseCategory",
            fetch = FetchType.LAZY
    )
    private List<Expense> expenses = new ArrayList<>();

    public ExpenseCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ExpenseCategory() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }
}
