package com.nexus.income;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateIncomeRequest(
        @Positive double amount,
        Instant paymentDate,
        @Positive Integer projectId,
        @Positive Long payerId
) { }
