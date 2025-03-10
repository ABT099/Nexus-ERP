package com.nexus.income;

import com.nexus.project.BasicProjectResponse;
import com.nexus.user.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class IncomeMapper {
    private final UserMapper userMapper;

    public IncomeMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public BasicIncomeResponse toBasicIncomeResponse(Income income) {
        return new BasicIncomeResponse(
                income.getId(),
                income.getAmount(),
                income.getPaymentDate().toString()
        );
    }

    public IncomeResponse toIncomeResponse(Income income) {
        BasicProjectResponse project = null;

        if (income.getProject() != null) {
            project = new BasicProjectResponse(
                    income.getProject().getId(),
                    income.getProject().getName()
            );
        }

        return new IncomeResponse(
                income.getId(),
                income.getAmount(),
                income.getPaymentDate().toString(),
                userMapper.toUserInfo(income.getPayer()),
                project
        );
    }
}
