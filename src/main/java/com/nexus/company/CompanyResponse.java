package com.nexus.company;

public record CompanyResponse(
        Long id,
        Long userId,
        String avatarUrl,
        String companyName
) { }
