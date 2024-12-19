package com.nexus.payment;

import com.nexus.abstraction.CreateFinancialRequest;
import jakarta.annotation.Nullable;

import java.time.ZonedDateTime;

public final class CreatePaymentRequest extends CreateFinancialRequest {

    private final Integer payerId;

    public CreatePaymentRequest(double Amount, ZonedDateTime paymentDate, @Nullable Integer projectId, Integer payerId) {
        super(Amount, paymentDate, projectId);
        this.payerId = payerId;
    }

    public Integer payerId() {
        return payerId;
    }
}
