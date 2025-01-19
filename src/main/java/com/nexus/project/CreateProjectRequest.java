package com.nexus.project;

import com.nexus.validation.AfterNow;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record CreateProjectRequest(
        @NotNull @Positive Long ownerId,
        @NotEmpty String name,
        @NotEmpty String description,
        @AfterNow ZonedDateTime startDate,
        @AfterNow ZonedDateTime expectedEndDate,
        @Positive double price
) { }
