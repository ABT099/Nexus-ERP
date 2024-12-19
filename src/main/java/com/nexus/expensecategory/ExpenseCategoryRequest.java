package com.nexus.expensecategory;

import jakarta.validation.constraints.NotEmpty;

public record ExpenseCategoryRequest(
        @NotEmpty
        String name,
        @NotEmpty
        String description
) { }
