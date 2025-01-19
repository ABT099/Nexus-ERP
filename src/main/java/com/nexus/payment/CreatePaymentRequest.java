package com.nexus.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record CreatePaymentRequest(
        @Positive double amount,
        ZonedDateTime paymentDate,
        @NotNull @Positive Integer projectId,
        @Positive Long payerId
) { }
