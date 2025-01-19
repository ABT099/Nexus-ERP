package com.nexus.event;

import java.time.ZonedDateTime;

public record BasicEventResponse(
        String name,
        String eventType,
        String status,
        ZonedDateTime date,
        boolean urgent
) { }
