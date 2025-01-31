package com.nexus.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreatePaymentRequest(
        @Positive double amount,
        Instant paymentDate,
        @NotNull @Positive Integer projectId,
        @Positive Long payerId
) { }
