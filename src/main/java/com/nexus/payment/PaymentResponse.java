package com.nexus.payment;

import com.nexus.user.UserInfoDTO;

public record PaymentResponse(
        Integer id,
        double amount,
        String paymentDate,
        UserInfoDTO payer
) { }
