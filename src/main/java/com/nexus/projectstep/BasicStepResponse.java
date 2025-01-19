package com.nexus.projectstep;

import com.nexus.common.Status;

import java.time.ZonedDateTime;

public record BasicStepResponse(
    Integer id,
    String name,
    String description,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    Status status
) { }
