package com.nexus.file;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record UpdateFileRequest(
        @Positive int id,
        @NotEmpty String name,
        String description
) { }
