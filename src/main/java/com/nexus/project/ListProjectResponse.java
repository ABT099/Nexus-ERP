package com.nexus.project;

import com.nexus.common.Status;

import java.time.ZonedDateTime;

public record ListProjectResponse(
    Integer id,
    String name,
    ZonedDateTime startDate,
    Status status
) { }
