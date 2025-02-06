package com.nexus.budget;

import com.nexus.expense.ExpenseResponse;
import com.nexus.income.BasicIncomeResponse;

import java.time.Instant;
import java.util.List;

public record BudgetResponse(
        String name,
        Instant startDate,
        Instant endDate,
        double budget,
        double currentTotal,
        double totalIncome,
        double totalExpense,
        List<BasicIncomeResponse> incomes,
        List<ExpenseResponse> expenses,
        String notice,
        boolean active,
        boolean archive
) { }
