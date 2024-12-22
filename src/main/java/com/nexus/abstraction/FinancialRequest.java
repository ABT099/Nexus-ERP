package com.nexus.abstraction;

import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public abstract class FinancialRequest {

    @Positive
    private final double amount;
    private final ZonedDateTime paymentDate;

    protected FinancialRequest(double amount, ZonedDateTime paymentDate) {
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    @Positive
    public double getAmount() {
        return amount;
    }

    public ZonedDateTime getPaymentDate() {
        return paymentDate;
    }
}
