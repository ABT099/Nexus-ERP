package com.nexus.expense;

import com.nexus.abstraction.CreateFinancialRequest;
import jakarta.annotation.Nullable;

import java.time.ZonedDateTime;

public final class CreateExpenseRequest extends CreateFinancialRequest {

    public CreateExpenseRequest(double Amount, ZonedDateTime paymentDate, @Nullable Integer projectId, @Nullable Integer expenseCategoryId) {
        super(Amount, paymentDate, projectId);
        this.expenseCategoryId = expenseCategoryId;
    }

    @Nullable
    private final Integer expenseCategoryId;

    public Integer expenseCategoryId() {
        return expenseCategoryId;
    }
}
