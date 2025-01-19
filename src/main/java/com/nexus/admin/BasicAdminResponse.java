package com.nexus.admin;

public record BasicAdminResponse (
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String avatarUrl
) { }
