package com.nexus.expense;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.expensecategory.ExpenseCategoryFinder;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.utils.UpdateHandler;
import com.nexus.zoned.Zoned;
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
    private final ExpenseCategoryFinder expenseCategoryFinder;
    private final ExpenseMapper expenseMapper;
    private final MonitorManager monitorManager;
    private final ExpenseService expenseService;

    public ExpenseController(
            ExpenseRepository expenseRepository,
            ExpenseCategoryFinder expenseCategoryFinder,
            ExpenseMapper expenseMapper,
            MonitorManager monitorManager,
            ExpenseService expenseService
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryFinder = expenseCategoryFinder;
        this.expenseMapper = expenseMapper;
        this.monitorManager = monitorManager;
        this.expenseService = expenseService;
    }

    @Zoned
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAll(
        @RequestParam(
            required = false,
            name = "a"
        ) ArchivableQueryType archived
    ) {
        List<Expense> expenses = ArchivedService.determine(archived, expenseRepository);

        return ResponseEntity.ok(expenses.stream()
                .map(expenseMapper::toExpenseResponse)
                .toList());
    }

    @Zoned
    @GetMapping("{id}")
    public ResponseEntity<ExpenseResponse> getById(@Valid @Positive @PathVariable int id) {
        Expense expense = expenseService.findById(id);

        return ResponseEntity.ok(expenseMapper.toExpenseResponse(expense));
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CreateExpenseRequest request) {
        Expense expense = expenseService.create(request);

        monitorManager.monitor(expense, ActionType.CREATE);

        return ResponseEntity.created(URI.create("expenses/" + expense.getId())).body(expense.getId());
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable long id, @Valid @RequestBody UpdateExpenseRequest request) {
        Expense expense = expenseService.findById(id);

        if (expense.isArchived()) {
            throw new NoUpdateException("Archived expense cannot be updated");
        }

        UpdateHandler.updateEntity(expense, tracker -> {
            tracker.updateField(
                    expense.getExpenseCategory()::getId,
                    request.expenseCategoryId(),
                    cId -> {
                        ExpenseCategory eCategory = expenseCategoryFinder.findById(cId);
                        expense.setExpenseCategory(eCategory);
                    });
            tracker.updateField(expense::getAmount, request.amount(), expense::setAmount);
            tracker.updateField(expense::getPaymentDate, request.paymentDate(), expense::setPaymentDate);
        }, () -> expenseRepository.save(expense), monitorManager);
    }

    @PatchMapping("archive/{id}")
    public void archive(@Valid @Positive @PathVariable long id) {
        Expense expense = expenseService.findById(id);

        if (expense.isArchived()) {
            throw new NoUpdateException("Expense is already archived");
        }

        expenseRepository.archiveById(id);

        monitorManager.monitor(expense, ActionType.ARCHIVE);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        Expense expense = expenseService.findById(id);

        expenseRepository.delete(expense);

        monitorManager.monitor(expense, ActionType.DELETE);
    }
}
