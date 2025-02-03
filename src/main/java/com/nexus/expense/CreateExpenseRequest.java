package com.nexus.expense;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateExpenseRequest(
        @Positive double amount,
        Instant paymentDate,
        Integer projectId,
        @Positive Long expenseCategoryId
) {
}
