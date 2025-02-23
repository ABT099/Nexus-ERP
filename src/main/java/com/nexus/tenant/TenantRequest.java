package com.nexus.tenant;

import java.util.List;

import com.nexus.common.person.CreatePersonRequest;

import jakarta.validation.constraints.NotBlank;

public record TenantRequest(
    @NotBlank String priceId,
    @NotBlank String name,
    @NotBlank String email,
    @NotBlank String phoneNumber,
    List<CreatePersonRequest> adminAccounts
) {}
