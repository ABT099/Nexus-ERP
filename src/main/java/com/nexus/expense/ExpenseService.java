package com.nexus.expense;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.expensecategory.ExpenseCategoryFinder;
import com.nexus.project.Project;
import com.nexus.project.ProjectService;
import com.nexus.tenant.TenantContext;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ProjectService projectService;
    private final ExpenseCategoryFinder expenseCategoryFinder;
    private final ExpenseRepository expenseRepository;

    public ExpenseService(
            ProjectService projectService,
            ExpenseCategoryFinder expenseCategoryFinder,
            ExpenseRepository expenseRepository
    ) {
        this.projectService = projectService;
        this.expenseCategoryFinder = expenseCategoryFinder;
        this.expenseRepository = expenseRepository;
    }


    public Expense findById(long id) {
        return expenseRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Expense with id " + id + " not found")
                );
    }

    public Expense create(CreateExpenseRequest request) {
        ExpenseCategory expenseCategory = expenseCategoryFinder.findById(request.expenseCategoryId());

        Expense expense;

        if (request.projectId() != null) {
            Project project = projectService.findById(request.projectId());

            expense = new Expense(request.amount(), request.paymentDate(), project, expenseCategory, TenantContext.getTenantId());
        } else {
            expense = new Expense(request.amount(), request.paymentDate(), expenseCategory, TenantContext.getTenantId());
        }

        expenseRepository.save(expense);

        return expense;
    }
}
