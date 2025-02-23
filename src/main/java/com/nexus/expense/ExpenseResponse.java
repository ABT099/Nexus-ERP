package com.nexus.expense;

import com.nexus.expensecategory.BasicExpenseCategoryResponse;

public record ExpenseResponse(
        Long id,
        long amount,
        String paymentDate,
        BasicExpenseCategoryResponse category,
        boolean archived
) { }
