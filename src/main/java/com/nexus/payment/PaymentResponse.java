package com.nexus.payment;

import com.nexus.user.UserInfoDTO;

public record PaymentResponse(
        Long id,
        double amount,
        String paymentDate,
        UserInfoDTO payer
) { }
