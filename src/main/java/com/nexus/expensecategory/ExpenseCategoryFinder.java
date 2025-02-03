package com.nexus.expensecategory;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ExpenseCategoryFinder {

    private final ExpenseCategoryRepository repository;

    public ExpenseCategoryFinder(ExpenseCategoryRepository repository) {
        this.repository = repository;
    }

    public ExpenseCategory findById(long id) {
        return repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("category not found with id " + id)
                );
    }
}
