package com.nexus.income;

public record BasicIncomeResponse(
        Long id,
        double amount,
        String paymentDate
) { }
