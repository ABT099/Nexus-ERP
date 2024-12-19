package com.nexus.expense;

import com.nexus.abstraction.FinancialRequest;

import java.time.ZonedDateTime;

public final class UpdateExpenseRequest extends FinancialRequest {
    private final Integer expenseCategoryId;

    public UpdateExpenseRequest(double amount, ZonedDateTime paymentDate, Integer expenseCategoryId) {
        super(amount, paymentDate);
        this.expenseCategoryId = expenseCategoryId;
    }

    public Integer expenseCategoryId() {
        return expenseCategoryId;
    }
}
