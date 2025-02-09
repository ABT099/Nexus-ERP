package com.nexus.projectstep;

import com.nexus.validation.AfterNow;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateProjectStepRequest(
        @NotNull @Positive Integer projectId,
        @NotEmpty String name,
        @NotEmpty String description,
        Instant startDate,
        @AfterNow Instant expectedEndDate
) {
}
