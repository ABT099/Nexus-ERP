package com.nexus.income;

import com.nexus.project.BasicProjectResponse;
import com.nexus.user.UserInfoDTO;

public record IncomeResponse(
        Long id,
        long amount,
        String paymentDate,
        UserInfoDTO payer,
        BasicProjectResponse project
) { }
