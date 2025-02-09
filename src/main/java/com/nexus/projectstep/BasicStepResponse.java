package com.nexus.projectstep;

import com.nexus.common.Status;

import java.time.Instant;

public record BasicStepResponse(
    Integer id,
    String name,
    String description,
    Instant startDate,
    Instant endDate,
    Status status
) { }
