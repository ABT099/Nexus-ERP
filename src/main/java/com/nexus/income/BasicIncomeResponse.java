package com.nexus.income;

public record BasicIncomeResponse(
        Long id,
        long amount,
        String paymentDate
) { }
