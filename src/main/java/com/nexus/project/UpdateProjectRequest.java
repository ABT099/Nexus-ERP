package com.nexus.project;

import com.nexus.validation.AfterNow;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record UpdateProjectRequest(
        @NotNull @Positive Integer id,
        @NotEmpty String name,
        @NotEmpty String description,
        Instant startDate,
        @AfterNow Instant expectedEndDate,
        Instant actualEndDate,
        @Positive double price
) { }
