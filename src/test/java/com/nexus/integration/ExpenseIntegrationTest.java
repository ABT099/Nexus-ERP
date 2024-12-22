package com.nexus.integration;

import com.nexus.expense.CreateExpenseRequest;
import com.nexus.expense.Expense;
import com.nexus.expense.UpdateExpenseRequest;
import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.expensecategory.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

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
        int categoryId = getCategoryId();

        int expenseId = createExpense(categoryId);

        getExpense(expenseId);
    }

    @Test
    void canUpdateExpense() {
        int categoryId = getCategoryId();
        int expenseId = createExpense(categoryId);

        Expense expense = getExpense(expenseId);

        UpdateExpenseRequest request = new UpdateExpenseRequest(111, ZonedDateTime.now(), null);

        webTestClient.put()
                .uri("/expenses/" + expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(request), UpdateExpenseRequest.class)
                .exchange()
                .expectStatus().isOk();

        Expense updatedExpense = getExpense(expenseId);

        assertNotNull(updatedExpense);

        assertNotEquals(expense.getAmount(), updatedExpense.getAmount());
        assertEquals(expense.getExpenseCategory().getId(), updatedExpense.getExpenseCategory().getId());
    }

    @Test
    void canUpdateExpenseWithCategory() {
        int categoryId = getCategoryId();
        int expenseId = createExpense(categoryId);

        Expense expense = getExpense(expenseId);

        int newCategoryId = getCategoryId();

        UpdateExpenseRequest request = new UpdateExpenseRequest(111, ZonedDateTime.now(), newCategoryId);

        webTestClient.put()
                .uri("/expenses/" + expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .body(Mono.just(request), UpdateExpenseRequest.class)
                .exchange()
                .expectStatus().isOk();

        Expense updatedExpense = getExpense(expenseId);

        assertNotNull(updatedExpense);
        assertNotEquals(expense.getAmount(), updatedExpense.getAmount());
        assertNotEquals(expense.getExpenseCategory().getId(), updatedExpense.getExpenseCategory().getId());
    }

    @Test
    void canDeleteExpense() {
        int categoryId = getCategoryId();
        int expenseId = createExpense(categoryId);

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

    private Integer createExpense(int expenseCategoryId) {
        CreateExpenseRequest request = new CreateExpenseRequest(
                123,
                ZonedDateTime.now(),
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

    private Expense getExpense(Integer expenseId) {
        Expense expense = webTestClient.get()
                .uri("/expenses/{id}", expenseId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Expense.class)
                .returnResult().getResponseBody();

        assertNotNull(expense);

        return expense;
    }

    private int getCategoryId() {
        ExpenseCategory expenseCategory = new ExpenseCategory(faker.name().name(), faker.name().nameWithMiddle());
        expenseCategoryRepo.save(expenseCategory);
        return expenseCategory.getId();
    }
}
