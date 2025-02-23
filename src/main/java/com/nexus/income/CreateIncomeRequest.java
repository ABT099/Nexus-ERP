package com.nexus.income;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateIncomeRequest(
        @Positive long amount,
        Instant paymentDate,
        @Positive Integer projectId,
        @Positive Long payerId,
        @NotEmpty String currency,
        String source,
        boolean isStripe
) { }
