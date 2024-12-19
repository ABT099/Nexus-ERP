package com.nexus.payment;

import com.nexus.abstraction.FinancialRequest;

import java.time.ZonedDateTime;

public final class UpdatePaymentRequest extends FinancialRequest {
    public UpdatePaymentRequest(double amount, ZonedDateTime paymentDate) {
        super(amount, paymentDate);
    }
}
