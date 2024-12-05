package com.nexus.event;

import com.nexus.validation.AfterNow;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.Set;

public record CreateEventRequest(
        @NotEmpty
        Set<Long> adminIds,
        @NotEmpty
        String name,
        @NotEmpty
        String description,
        @NotNull
        EventType type,
        @NotNull
        @AfterNow
        ZonedDateTime date
) { }
