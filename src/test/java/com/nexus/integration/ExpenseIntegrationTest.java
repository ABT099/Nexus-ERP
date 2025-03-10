package com.nexus.integration;

import com.nexus.expense.CreateExpenseRequest;
import com.nexus.expense.Expense;
import com.nexus.expense.ExpenseResponse;
import com.nexus.expense.UpdateExpenseRequest;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.expensecategory.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepo;

    @BeforeEach
    public void setup() {
        createUser();
    }

    @Test
    void canCreateExpense() {
        long categoryId = getCategoryId();

        long expenseId = createExpense(categoryId);

        getExpense(expenseId);
    }

    @Test
    void canUpdateExpense() {
        long categoryId = getCategoryId();
        long expenseId = createExpense(categoryId);

        ExpenseResponse expense = getExpense(expenseId);

        UpdateExpenseRequest request = new UpdateExpenseRequest(111, Instant.now().now(), getCategoryId());

        webTestClient.put()
                .uri("/expenses/" + expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(request), UpdateExpenseRequest.class)
                .exchange()
                .expectStatus().isOk();

        ExpenseResponse updatedExpense = getExpense(expenseId);

        assertNotNull(updatedExpense);

        assertNotEquals(expense.amount(), updatedExpense.amount());
    }

    @Test
    void canUpdateExpenseWithCategory() {
        long categoryId = getCategoryId();
        long expenseId = createExpense(categoryId);

        ExpenseResponse expense = getExpense(expenseId);

        long newCategoryId = getCategoryId();

        UpdateExpenseRequest request = new UpdateExpenseRequest(111, Instant.now(), newCategoryId);

        webTestClient.put()
                .uri("/expenses/" + expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(request), UpdateExpenseRequest.class)
                .exchange()
                .expectStatus().isOk();

        ExpenseResponse updatedExpense = getExpense(expenseId);

        assertNotNull(updatedExpense);
        assertNotEquals(expense.amount(), updatedExpense.amount());
    }

    @Test
    void canDeleteExpense() {
        long categoryId = getCategoryId();
        long expenseId = createExpense(categoryId);

        getExpense(expenseId);

        webTestClient.delete()
                .uri("/expenses/" + expenseId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/expenses/{id}", expenseId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isNotFound();
    }

    private Integer createExpense(long expenseCategoryId) {
        CreateExpenseRequest request = new CreateExpenseRequest(
                123,
                Instant.now(),
                null,
                expenseCategoryId
        );

        Integer expenseId = webTestClient.post()
                .uri("/expenses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(request), CreateExpenseRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Integer.class)
                .returnResult().getResponseBody();

        assertNotNull(expenseId);

        return expenseId;
    }

    private ExpenseResponse getExpense(Long expenseId) {
        ExpenseResponse expense = webTestClient.get()
                .uri("/expenses/{id}", expenseId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExpenseResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(expense);

        return expense;
    }

    private Long getCategoryId() {
        ExpenseCategory expenseCategory = new ExpenseCategory(faker.name().name(), faker.name().nameWithMiddle());
        expenseCategoryRepo.save(expenseCategory);
        return expenseCategory.getId();
    }
}
