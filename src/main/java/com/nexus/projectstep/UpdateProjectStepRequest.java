package com.nexus.projectstep;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record UpdateProjectStepRequest(
        @NotNull @Positive Integer id,
        @NotEmpty String name,
        @NotEmpty String description,
        Instant startDate,
        Instant expectedEndDate,
        Instant actualEndDate
) { }
