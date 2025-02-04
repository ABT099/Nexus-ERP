package com.nexus.expense;

import com.nexus.expensecategory.BasicExpenseCategoryResponse;

public record ExpenseResponse(
        Long id,
        double amount,
        String paymentDate,
        BasicExpenseCategoryResponse category,
        boolean archived
) { }
