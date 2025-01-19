package com.nexus.user;

public record UserInfoDTO(
    long id,
    String username,
    String avatarUrl
) { }
