package com.nexus.projectstep;

import com.nexus.abstraction.UpdateWorkItemRequest;

import java.time.ZonedDateTime;

public final class UpdateProjectStepRequest extends UpdateWorkItemRequest {
    public UpdateProjectStepRequest(int id, String name, String description, ZonedDateTime StartDate, ZonedDateTime ExpectedEndDate, ZonedDateTime actualEndDate) {
        super(id, name, description, StartDate, ExpectedEndDate, actualEndDate);
    }
}
