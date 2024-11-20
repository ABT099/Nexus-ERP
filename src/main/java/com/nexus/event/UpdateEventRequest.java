package com.nexus.event;

import com.nexus.common.Status;
import com.nexus.validation.EnumValue;
import jakarta.validation.constraints.NotEmpty;

public record UpdateEventRequest(
        @NotEmpty
        String name,
        @NotEmpty
        String description,
        @EnumValue(enumClass = EventType.class)
        EventType type,
        @EnumValue(enumClass = Status.class)
        Status status
) { }
