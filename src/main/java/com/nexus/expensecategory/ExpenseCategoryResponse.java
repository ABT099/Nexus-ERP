package com.nexus.expensecategory;

public record ExpenseCategoryResponse(
        Long id,
        String name,
        String description
) {
}
