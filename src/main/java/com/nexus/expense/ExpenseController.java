package com.nexus.expense;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.expensecategory.ExpenseCategoryFinder;
import com.nexus.project.Project;
import com.nexus.project.ProjectFinder;
import com.nexus.utils.UpdateHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final ProjectFinder projectFinder;
    private final ExpenseCategoryFinder expenseCategoryFinder;

    public ExpenseController(ExpenseRepository expenseRepository, ProjectFinder projectFinder, ExpenseCategoryFinder expenseCategoryFinder) {
        this.expenseRepository = expenseRepository;
        this.projectFinder = projectFinder;
        this.expenseCategoryFinder = expenseCategoryFinder;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAll() {
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Expense> getById(@Valid @Positive @PathVariable int id) {
        Expense expense = findById(id);

        System.out.println(expense);

        return ResponseEntity.ok(expense);
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody CreateExpenseRequest request) {
        ExpenseCategory expenseCategory = expenseCategoryFinder.findById(request.getExpenseCategoryId());

        Expense expense;

        if (request.getProjectId() != null) {
            Project project = projectFinder.findById(request.getProjectId());

            expense = new Expense(request.getAmount(), request.getPaymentDate(), project, expenseCategory);
        } else {
            expense = new Expense(request.getAmount(), request.getPaymentDate(), expenseCategory);
        }

        expenseRepository.save(expense);

        return ResponseEntity.created(URI.create("expenses/" + expense.getId())).body(expense.getId());
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable int id, @Valid @RequestBody UpdateExpenseRequest request) {
        Expense expense = findById(id);

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(
                    expense.getExpenseCategory()::getId,
                    request.getExpenseCategoryId(),
                    cId -> {
                        ExpenseCategory eCategory = expenseCategoryFinder.findById(cId);
                        expense.setExpenseCategory(eCategory);
                    });
            tracker.updateField(expense::getAmount, request.getAmount(), expense::setAmount);
            tracker.updateField(expense::getPaymentDate, request.getPaymentDate(), expense::setPaymentDate);
        }, () -> expenseRepository.save(expense));
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        expenseRepository.deleteById(id);
    }

    private Expense findById(int id) {
        return expenseRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Expense with id " + id + " not found")
                );
    }
}
