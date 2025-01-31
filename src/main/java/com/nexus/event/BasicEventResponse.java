package com.nexus.event;

public record BasicEventResponse(
        String name,
        String eventType,
        String status,
        String date,
        boolean urgent
) { }
