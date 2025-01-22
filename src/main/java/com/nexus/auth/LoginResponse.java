package com.nexus.auth;

public record LoginResponse(
        String token,
        String tenantId
) { }
