package com.nexus.expense;

import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record CreateExpenseRequest(
        @Positive double amount,
        ZonedDateTime paymentDate,
        Integer projectId,
        @Positive int expenseCategoryId
) {
}
