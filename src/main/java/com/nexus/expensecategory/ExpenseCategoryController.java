package com.nexus.expensecategory;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.utils.UpdateHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("expense-categories")
public class ExpenseCategoryController {

    private final ExpenseCategoryRepository repository;
    private final ExpenseCategoryFinder finder;
    private final ExpenseCategoryMapper mapper;

    public ExpenseCategoryController(ExpenseCategoryRepository repository, ExpenseCategoryFinder finder, ExpenseCategoryMapper mapper) {
        this.repository = repository;
        this.finder = finder;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<BasicExpenseCategoryResponse>> getAll() {
        return ResponseEntity.ok(
                repository.findAll().stream()
                        .map(mapper::toBasicExpenseCategoryResponse)
                        .toList()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<ExpenseCategoryResponse> getById(@Valid @Positive @PathVariable int id) {
        ExpenseCategory category = finder.findById(id);

        return ResponseEntity.ok(mapper.toExpenseCategoryResponse(category));
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody ExpenseCategoryRequest request) {
        ExpenseCategory eCategory = new ExpenseCategory(request.name(), request.description());
        repository.save(eCategory);

        return ResponseEntity.created(URI.create("expense-categories/" + eCategory.getId())).body(eCategory.getId());
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable int id, @Valid @RequestBody ExpenseCategoryRequest request) {
        ExpenseCategory eCategory = finder.findById(id);

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(eCategory::getName, request.name(), eCategory::setName);
            tracker.updateField(eCategory::getDescription, request.description(), eCategory::setDescription);
        }, () -> repository.save(eCategory));
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        repository.deleteById(id);
    }
}
