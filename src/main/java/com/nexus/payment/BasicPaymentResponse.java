package com.nexus.payment;

import java.time.Instant;

public record BasicPaymentResponse(
        Integer id,
        double amount,
        String paymentDate
) { }
