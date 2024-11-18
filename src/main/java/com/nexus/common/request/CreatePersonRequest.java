package com.nexus.common.request;

public record CreatePersonRequest(
        String firstName,
        String lastName,
        String username,
        String password
) { }
