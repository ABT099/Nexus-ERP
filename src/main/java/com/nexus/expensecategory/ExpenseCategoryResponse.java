package com.nexus.expensecategory;

public record ExpenseCategoryResponse(
        Integer id,
        String name,
        String description
) {
}
