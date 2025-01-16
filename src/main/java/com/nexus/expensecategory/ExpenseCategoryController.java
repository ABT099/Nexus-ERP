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

    public ExpenseCategoryController(ExpenseCategoryRepository repository, ExpenseCategoryFinder finder) {
        this.repository = repository;
        this.finder = finder;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseCategory>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<ExpenseCategory> getById(@Valid @Positive @PathVariable int id) {
        return ResponseEntity.ok(finder.findById(id));
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
