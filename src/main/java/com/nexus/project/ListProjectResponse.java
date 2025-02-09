package com.nexus.project;

import com.nexus.common.Status;

import java.time.Instant;

public record ListProjectResponse(
    Integer id,
    String name,
    Instant startDate,
    Status status
) { }
