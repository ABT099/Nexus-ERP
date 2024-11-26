package com.nexus.user;

public record UserDto(
        User user,
        String token
) { }
