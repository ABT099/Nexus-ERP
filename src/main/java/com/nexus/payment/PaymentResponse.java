package com.nexus.payment;

import com.nexus.user.UserInfoDTO;

import java.time.ZonedDateTime;

public record PaymentResponse(
        Integer id,
        double amount,
        ZonedDateTime paymentDate,
        UserInfoDTO payer
) { }
