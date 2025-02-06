package com.nexus.budget;

import java.time.Instant;

public record ListBudgetResponse(
        String name,
        double budget,
        double currentTotal,
        Instant startDate,
        Instant endDate
) { }
