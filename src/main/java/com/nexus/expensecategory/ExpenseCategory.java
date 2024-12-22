package com.nexus.expensecategory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexus.expense.Expense;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

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
    @JsonIgnore
    private List<Expense> expenses = new ArrayList<>();

    public ExpenseCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ExpenseCategory() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
