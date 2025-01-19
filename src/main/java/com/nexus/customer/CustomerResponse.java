package com.nexus.customer;

public record CustomerResponse(
        Long id,
        Long userId,
        String avatarUrl,
        String firstName,
        String lastName
) { }
