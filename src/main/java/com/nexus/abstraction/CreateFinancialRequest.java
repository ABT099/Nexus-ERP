package com.nexus.abstraction;

import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public abstract class CreateFinancialRequest extends FinancialRequest {

    private final Integer projectId;

    public CreateFinancialRequest(
            @Positive double amount,
            ZonedDateTime paymentDate,
            Integer projectId
    ) {
        super(amount, paymentDate);
        this.projectId = projectId;
    }

    public Integer getProjectId() {
        return projectId;
    }
}
