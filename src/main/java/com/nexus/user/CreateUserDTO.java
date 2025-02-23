package com.nexus.user;

public record CreateUserDTO(
    String username,
    String password,
    UserType userType
) {}
