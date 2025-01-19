package com.nexus.admin;

import com.nexus.event.BasicEventResponse;

import java.util.Set;

public record AdminResponse(
        Long id,
        Long userId,
        String avatarUrl,
        String firstName,
        String lastName,
        Set<BasicEventResponse> events
) { }
