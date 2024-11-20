package com.nexus.event;

import com.nexus.validation.EnumValue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateEventRequest(
        @NotEmpty
        Set<Long> adminIds,
        @NotEmpty
        String name,
        @NotEmpty
        String description,
        @NotNull
        @EnumValue(enumClass = EventType.class)
        EventType type
) { }
