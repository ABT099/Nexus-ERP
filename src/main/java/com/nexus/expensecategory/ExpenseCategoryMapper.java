package com.nexus.expensecategory;

import org.springframework.stereotype.Component;

@Component
public class ExpenseCategoryMapper {

    public BasicExpenseCategoryResponse toBasicExpenseCategoryResponse(ExpenseCategory expenseCategory) {
        return new BasicExpenseCategoryResponse(
                expenseCategory.getId(),
                expenseCategory.getName()
        );
    }

    public ExpenseCategoryResponse toExpenseCategoryResponse(ExpenseCategory expenseCategory) {
        return new ExpenseCategoryResponse(
                expenseCategory.getId(),
                expenseCategory.getName(),
                expenseCategory.getDescription()
        );
    }
}
