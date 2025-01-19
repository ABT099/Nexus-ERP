package com.nexus.payment;

import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record UpdatePaymentRequest(
        @Positive double amount,
        ZonedDateTime paymentDate
) { }
