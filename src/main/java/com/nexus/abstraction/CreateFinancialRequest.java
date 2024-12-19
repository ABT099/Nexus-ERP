package com.nexus.abstraction;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public abstract class CreateFinancialRequest extends FinancialRequest {

    @Nullable
    private final Integer projectId;

    public CreateFinancialRequest(
            @Positive double Amount,
            ZonedDateTime paymentDate,
            @Nullable Integer projectId
    ) {
        super(Amount, paymentDate);
        this.projectId = projectId;
    }

    @Nullable
    public Integer projectId() {
        return projectId;
    }
}
