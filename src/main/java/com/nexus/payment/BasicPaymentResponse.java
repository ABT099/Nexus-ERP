package com.nexus.payment;

public record BasicPaymentResponse(
        Long id,
        double amount,
        String paymentDate
) { }
