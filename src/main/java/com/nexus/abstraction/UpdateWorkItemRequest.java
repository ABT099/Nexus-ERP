package com.nexus.abstraction;

import com.nexus.validation.AfterNow;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public abstract class UpdateWorkItemRequest extends CreateWorkItemRequest {
    private final @Positive int id;
    @AfterNow
    private final ZonedDateTime actualEndDate;

    public UpdateWorkItemRequest(
            @Positive int id,
            @NotEmpty
            String name,
            @NotEmpty
            String description,
            ZonedDateTime StartDate,
            ZonedDateTime ExpectedEndDate,
            ZonedDateTime actualEndDate) {
        super(name, description, StartDate, ExpectedEndDate);
        this.id = id;
        this.actualEndDate = actualEndDate;
    }

    public @Positive int id() {
        return id;
    }

    @AfterNow
    public ZonedDateTime actualEndDate() {
        return actualEndDate;
    }
}
