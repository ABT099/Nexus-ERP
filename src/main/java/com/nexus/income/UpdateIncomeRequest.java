package com.nexus.income;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record UpdateIncomeRequest(
        @Positive double amount,
        Instant paymentDate
) { }
