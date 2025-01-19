package com.nexus.expense;

import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record UpdateExpenseRequest(
        @Positive double amount,
        ZonedDateTime paymentDate,
        @Positive int expenseCategoryId
) {
}
