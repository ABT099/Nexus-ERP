package com.nexus.auth;

public record LoginRequest(
        String username,
        String password
) { }
