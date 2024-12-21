package com.nexus.event;

import com.nexus.common.Status;
import com.nexus.validation.AfterNow;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record UpdateEventRequest(
        @NotEmpty
        String name,
        @NotEmpty
        String description,
        @NotNull
        EventType type,
        Status status,
        @NotNull
        @AfterNow
        ZonedDateTime date
) { }
