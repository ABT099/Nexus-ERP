package com.nexus.expense;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record UpdateExpenseRequest(
        @Positive long amount,
        Instant paymentDate,
        @Positive Long expenseCategoryId
) {
}
