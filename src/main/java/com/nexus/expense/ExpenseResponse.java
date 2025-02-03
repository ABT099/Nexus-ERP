package com.nexus.expense;

import com.nexus.expensecategory.BasicExpenseCategoryResponse;

public record ExpenseResponse(
        long id,
        double amount,
        String paymentDate,
        BasicExpenseCategoryResponse category
) { }
