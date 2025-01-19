package com.nexus.expense;

import com.nexus.expensecategory.BasicExpenseCategoryResponse;

import java.time.ZonedDateTime;

public record ExpenseResponse(
        Integer id,
        double amount,
        ZonedDateTime paymentDate,
        BasicExpenseCategoryResponse category
) { }
