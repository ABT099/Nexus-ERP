package com.nexus.budget;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record BudgetRequest(
        String name,
        Instant startDate,
        Instant endDate,
        @Positive
        double budget,
        double currentTotal,
        boolean active
) { }
