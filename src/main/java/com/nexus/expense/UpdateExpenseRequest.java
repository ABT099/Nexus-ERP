package com.nexus.expense;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record UpdateExpenseRequest(
        @Positive double amount,
        Instant paymentDate,
        @Positive int expenseCategoryId
) {
}
