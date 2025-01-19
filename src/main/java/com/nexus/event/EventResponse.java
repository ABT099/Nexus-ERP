package com.nexus.event;

import java.time.ZonedDateTime;

public record EventResponse(
        String name,
        String description,
        String eventType,
        String status,
        ZonedDateTime date
) { }
