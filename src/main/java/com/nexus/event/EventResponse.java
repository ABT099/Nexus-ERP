package com.nexus.event;

public record EventResponse(
        String name,
        String description,
        String eventType,
        String status,
        String date,
        boolean urgent,
        boolean archived
) { }
