package com.nexus.user;

public record UserInfoDTO(
    Long id,
    String username,
    String avatarUrl
) { }
