package com.nexus.common.person;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record CreatePersonRequest(
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @NotEmpty
        String username,
        @NotEmpty
        @Length(min = 8, max = 25)
        String password
) { }
