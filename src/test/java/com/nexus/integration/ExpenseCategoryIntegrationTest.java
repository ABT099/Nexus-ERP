package com.nexus.integration;

import com.nexus.expensecategory.ExpenseCategory;
import com.nexus.expensecategory.ExpenseCategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseCategoryIntegrationTest extends AuthenticatedIntegrationTest {

    @BeforeEach
    public void setUp() {
        createUser();
    }

    @Test
    void canCreateExpenseCategory() {
        int categoryId = createExpenseCategory();

        ExpenseCategory expenseCategory = getExpenseCategory(categoryId);

        assertNotNull(expenseCategory);
    }

    @Test
    void canUpdateExpenseCategory() {
        int categoryId = createExpenseCategory();

        ExpenseCategory oldCategory = getExpenseCategory(categoryId);

        ExpenseCategoryRequest request = new ExpenseCategoryRequest("name12", "description12");

        webTestClient.put()
                .uri("/expense-categories/{id}", categoryId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ExpenseCategory.class)
                .exchange()
                .expectStatus().isOk();

        ExpenseCategory newCategory = getExpenseCategory(categoryId);

        assertNotNull(newCategory);

        assertNotEquals(oldCategory.getName(), newCategory.getName());
        assertNotEquals(oldCategory.getDescription(), newCategory.getDescription());
    }

    @Test
    void canDeleteExpenseCategory() {
        int categoryId = createExpenseCategory();

        webTestClient.delete()
                .uri("/expense-categories/{id}", categoryId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("expense-categories/{id}", categoryId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isNotFound();
    }

    private int createExpenseCategory() {
        ExpenseCategoryRequest request = new ExpenseCategoryRequest("name", "description");

        Integer categoryId = webTestClient.post()
                .uri("/expense-categories")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ExpenseCategoryRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Integer.class)
                .returnResult().getResponseBody();

        assertNotNull(categoryId);

        return categoryId;
    }

    private ExpenseCategory getExpenseCategory(int categoryId) {
        return webTestClient.get()
                .uri("expense-categories/{id}", categoryId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExpenseCategory.class)
                .returnResult().getResponseBody();
    }
}
