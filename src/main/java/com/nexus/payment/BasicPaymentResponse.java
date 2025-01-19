package com.nexus.payment;

import java.time.ZonedDateTime;

public record BasicPaymentResponse(
        Integer id,
        double amount,
        ZonedDateTime paymentDate
) { }
