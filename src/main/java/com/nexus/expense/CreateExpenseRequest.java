package com.nexus.expense;

import com.nexus.abstraction.CreateFinancialRequest;
import jakarta.annotation.Nullable;

import java.time.ZonedDateTime;

public final class CreateExpenseRequest extends CreateFinancialRequest {

    public CreateExpenseRequest(double amount, ZonedDateTime paymentDate, @Nullable Integer projectId, Integer expenseCategoryId) {
        super(amount, paymentDate, projectId);
        this.expenseCategoryId = expenseCategoryId;
    }

    private final Integer expenseCategoryId;

    public Integer getExpenseCategoryId() {
        return expenseCategoryId;
    }
}
