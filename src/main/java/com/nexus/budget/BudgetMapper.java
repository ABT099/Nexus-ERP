package com.nexus.budget;

import com.nexus.expense.ExpenseMapper;
import com.nexus.income.IncomeMapper;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    private final IncomeMapper incomeMapper;
    private final ExpenseMapper expenseMapper;

    public BudgetMapper(IncomeMapper incomeMapper, ExpenseMapper expenseMapper) {
        this.incomeMapper = incomeMapper;
        this.expenseMapper = expenseMapper;
    }

    public BudgetResponse toBudgetResponse(Budget budget) {
        return new BudgetResponse(
                budget.getName(),
                budget.getStartDate(),
                budget.getEndDate(),
                budget.getBudget(),
                budget.getCurrentTotal(),
                budget.getTotalIncome(),
                budget.getTotalExpense(),
                budget.getIncomes().stream().map(incomeMapper::toBasicIncomeResponse).toList(),
                budget.getExpenses().stream().map(expenseMapper::toExpenseResponse).toList(),
                budget.getNotice(),
                budget.isActive(),
                budget.isArchived()
        );
    }

    public ListBudgetResponse toListBudgetResponse(Budget budget) {
        return new ListBudgetResponse(
                budget.getName(),
                budget.getBudget(),
                budget.getCurrentTotal(),
                budget.getStartDate(),
                budget.getEndDate()
        );
    }
}
