package com.nexus.budget;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.expense.CreateExpenseRequest;
import com.nexus.expense.Expense;
import com.nexus.expense.ExpenseService;
import com.nexus.income.CreateIncomeRequest;
import com.nexus.income.Income;
import com.nexus.income.IncomeService;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.tenant.TenantContext;
import com.nexus.utils.UpdateHandler;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("budgets")
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final MonitorManager monitorManager;
    private final BudgetMapper budgetMapper;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    public BudgetController(
            BudgetRepository budgetRepository,
            MonitorManager monitorManager,
            BudgetMapper budgetMapper,
            IncomeService incomeService,
            ExpenseService expenseService
    ) {
        this.budgetRepository = budgetRepository;
        this.monitorManager = monitorManager;
        this.budgetMapper = budgetMapper;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<ListBudgetResponse>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            ) ArchivableQueryType archived
    ) {
        List<Budget> result = ArchivedService.determine(archived, budgetRepository);

        return ResponseEntity.ok(
                result.stream()
                        .map(budgetMapper::toListBudgetResponse)
                        .toList()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<BudgetResponse> getById(@Valid @Positive @PathVariable Long id) {
        Budget budget = findById(id);

        return ResponseEntity.ok(budgetMapper.toBudgetResponse(budget));
    }



    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody BudgetRequest request) {
        Budget budget = new Budget(
                request.name(),
                request.startDate(),
                request.endDate(),
                request.budget(),
                request.currentTotal(),
                request.active(),
                TenantContext.getTenantId()
        );

        budgetRepository.save(budget);

        monitorManager.monitor(budget, ActionType.CREATE);

        return ResponseEntity.created(URI.create("/budgets/" + budget.getId())).body(budget.getId());
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable Long id, @Valid @RequestBody BudgetRequest request) {
        Budget budget = findById(id);

        if (budget.isArchived()) {
            throw new NoUpdateException("Archived budget cannot be updated");
        }

        UpdateHandler.updateEntity(budget, tracker -> {
            tracker.updateField(budget::getName, request.name(), budget::setName);
            tracker.updateField(budget::getStartDate, request.startDate(), budget::setStartDate);
            tracker.updateField(budget::getEndDate, request.endDate(), budget::setEndDate);
            tracker.updateField(budget::getBudget, request.budget(), budget::setBudget);
            tracker.updateField(budget::getCurrentTotal, request.currentTotal(), budget::setCurrentTotal);
            tracker.updateField(budget::isActive, request.active(), budget::setActive);
        }, () -> budgetRepository.save(budget), monitorManager);
    }

    @PatchMapping("{id}")
    public void archive(@Valid @Positive @PathVariable Long id) {
        Budget budget = findById(id);

        if (budget.isArchived()) {
            throw new NoUpdateException("Budget is already archived");
        }

        budget.setArchived(true);

        budgetRepository.save(budget);

        monitorManager.monitor(budget, ActionType.ARCHIVE);
    }

    @Transactional
    @PatchMapping("{id}/add-income")
    public void addIncome(@Valid @Positive @PathVariable Long id, @Valid @RequestBody CreateIncomeRequest request) {
        Budget budget = findById(id);

        if (budget.isArchived()) {
            throw new NoUpdateException("Archived budget cannot be updated");
        }

        Income income = incomeService.create(request);

        budget.addPayment(income);

        budgetRepository.save(budget);

        monitorManager.monitor(budget, ActionType.ADD_PAYMENT);
    }

    @Transactional
    @PatchMapping("{budgetId}/remove-income/{incomeId}")
    public void removeIncome(
            @Valid @Positive @PathVariable Long budgetId,
            @Valid @Positive @PathVariable Integer incomeId
    ) {
        Budget budget = findById(budgetId);

        if (budget.isArchived()) {
            throw new NoUpdateException("Archived budget cannot be updated");
        }

        Income income = incomeService.findById(incomeId);

        budget.removePayment(income);

        budgetRepository.save(budget);

        monitorManager.monitor(budget, ActionType.REMOVE_PAYMENT);
    }

    @Transactional
    @PatchMapping("{id}/add-expense")
    public void addExpense(@Valid @Positive @PathVariable Long id, @Valid @RequestBody CreateExpenseRequest request) {
        Budget budget = findById(id);

        if (budget.isArchived()) {
            throw new NoUpdateException("Archived budget cannot be updated");
        }

        Expense expense = expenseService.create(request);

        budget.addPayment(expense);

        budgetRepository.save(budget);

        monitorManager.monitor(budget, ActionType.ADD_PAYMENT);
    }

    @Transactional
    @PatchMapping("{budgetId}/remove-expense/{expenseId}")
    public void removeExpense(
            @Valid @Positive @PathVariable Long budgetId,
            @Valid @Positive @PathVariable Integer expenseId
    ) {
        Budget budget = findById(budgetId);

        if (budget.isArchived()) {
            throw new NoUpdateException("Archived budget cannot be updated");
        }

        Expense expense = expenseService.findById(expenseId);

        budget.removePayment(expense);

        budgetRepository.save(budget);

        monitorManager.monitor(budget, ActionType.REMOVE_PAYMENT);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable Long id) {
        Budget budget = findById(id);

        budgetRepository.deleteById(id);

        monitorManager.monitor(budget, ActionType.DELETE);
    }

    private Budget findById(Long id) {
        return budgetRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Budget not found with id: " + id)
        );
    }
}
