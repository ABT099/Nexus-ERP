package com.nexus.income;

import com.nexus.user.UserInfoDTO;

public record IncomeResponse(
        Long id,
        double amount,
        String paymentDate,
        UserInfoDTO payer
) { }
