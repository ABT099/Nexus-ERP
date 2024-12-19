package com.nexus.abstraction;

import com.nexus.validation.AfterNow;
import jakarta.validation.constraints.NotEmpty;

import java.time.ZonedDateTime;

public abstract class CreateWorkItemRequest {
    private final @NotEmpty String name;
    private final @NotEmpty String description;
    @AfterNow
    private final ZonedDateTime startDate;
    @AfterNow
    private final ZonedDateTime expectedEndDate;

    public CreateWorkItemRequest(
            @NotEmpty
            String name,
            @NotEmpty
            String description,
            ZonedDateTime StartDate,
            ZonedDateTime ExpectedEndDate
    ) {
        this.name = name;
        this.description = description;
        this.startDate = StartDate;
        this.expectedEndDate = ExpectedEndDate;
    }

    public @NotEmpty String name() {
        return name;
    }

    public @NotEmpty String description() {
        return description;
    }

    @AfterNow
    public ZonedDateTime startDate() {
        return startDate;
    }

    @AfterNow
    public ZonedDateTime expectedEndDate() {
        return expectedEndDate;
    }
}
