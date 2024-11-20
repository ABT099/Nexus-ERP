package com.nexus.company;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record UpdateCompanyRequest(
        @Positive
        long id,
        @NotEmpty
        String companyName
) {}