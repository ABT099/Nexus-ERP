package com.nexus.payment;

import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record UpdatePaymentRequest(
        @Positive double amount,
        Instant paymentDate
) { }
