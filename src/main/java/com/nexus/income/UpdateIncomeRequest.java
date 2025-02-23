package com.nexus.income;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record UpdateIncomeRequest(
        @Positive long amount,
        Instant paymentDate
) { }
