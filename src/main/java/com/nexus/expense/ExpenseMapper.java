package com.nexus.expense;

import com.nexus.expensecategory.ExpenseCategoryMapper;
import com.nexus.project.ProjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
    private final ExpenseCategoryMapper expenseCategoryMapper;

    public ExpenseMapper(ExpenseCategoryMapper expenseCategoryMapper) {
        this.expenseCategoryMapper = expenseCategoryMapper;
    }

    public ExpenseResponse toExpenseResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getPaymentDate(),
                expenseCategoryMapper.toBasicExpenseCategoryResponse(expense.getExpenseCategory())
        );
    }
}
