package com.nexus.company;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record CreateCompanyRequest(
        @NotEmpty
        String companyName,
        @NotEmpty
        String username,
        @NotEmpty
        @Length(min = 8, max = 25)
        String password
) { }
