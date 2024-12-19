package com.nexus.projectstep;

import com.nexus.abstraction.CreateWorkItemRequest;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public final class CreateProjectStepRequest extends CreateWorkItemRequest {
    @Positive
    private final int projectId;
    public CreateProjectStepRequest(int projectId, String name, String description, ZonedDateTime StartDate, ZonedDateTime ExpectedEndDate) {
        super(name, description, StartDate, ExpectedEndDate);
        this.projectId = projectId;
    }

    public @Positive int projectId() {
        return projectId;
    }
}
