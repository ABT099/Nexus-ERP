package com.nexus.integration;

import com.nexus.budget.BudgetRequest;
import com.nexus.budget.BudgetResponse;
import com.nexus.expense.CreateExpenseRequest;
import com.nexus.expensecategory.ExpenseCategoryRequest;
import com.nexus.income.CreateIncomeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetIntegrationTest extends AuthenticatedIntegrationTest {

    private long budgetId;

    @BeforeEach
    public void setup() {
        createUser();

        BudgetRequest request = new BudgetRequest(
                "budget name",
                Instant.now(),
                Instant.now().plus(2, ChronoUnit.DAYS),
                1000.0,
                0,
                true
        );

        Long id = webTestClient.post()
                .uri("/budgets")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), BudgetRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(id);
        assertTrue(id > 0);

        budgetId = id;
    }

    @Test
    void canUpdateBudget() {
        BudgetResponse budget = getBudget();

        assertNotNull(budget);

        BudgetRequest request = new BudgetRequest(
                "newName",
                Instant.now(),
                Instant.now().plus(2, ChronoUnit.DAYS),
                2000.0,
                0,
                true
        );

        webTestClient.put()
                .uri("/budgets/{id}", budgetId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), BudgetRequest.class)
                .exchange()
                .expectStatus().isOk();

        BudgetResponse newBudget = getBudget();

        assertNotNull(newBudget);

        assertNotEquals(newBudget.name(), budget.name());
        assertNotEquals(newBudget.budget(), budget.budget());
    }

    @Test
    void canArchiveBudget() {
        BudgetResponse budget = getBudget();

        assertNotNull(budget);
        assertFalse(budget.archive());

        webTestClient.patch()
                .uri("/budgets/{id}", budgetId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        BudgetResponse newBudget = getBudget();

        assertNotNull(newBudget);
        assertTrue(newBudget.archive());
    }

    @Test
    void canAddIncome() {
        BudgetResponse budget = getBudget();

        assertNotNull(budget);

        CreateIncomeRequest incomeRequest = new CreateIncomeRequest(1234, Instant.now(), null, user.getId());

        webTestClient.patch()
                .uri("/budgets/{budgetId}/add-income", budgetId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(incomeRequest), CreateIncomeRequest.class)
                .exchange()
                .expectStatus().isOk();

        BudgetResponse newBudget = getBudget();

        assertNotNull(newBudget);
        assertFalse(newBudget.incomes().isEmpty());

        assertEquals(1234, newBudget.totalIncome());

        assertEquals(1234, newBudget.currentTotal());
    }

    @Test
    void canRemoveIncome() {
        BudgetResponse budget = getBudget();

        assertNotNull(budget);

        CreateIncomeRequest incomeRequest = new CreateIncomeRequest(1234, Instant.now(), null, user.getId());

        webTestClient.patch()
                .uri("/budgets/{budgetId}/add-income", budgetId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(incomeRequest), CreateIncomeRequest.class)
                .exchange()
                .expectStatus().isOk();

        BudgetResponse newBudget = getBudget();

        assertNotNull(newBudget);
        assertFalse(newBudget.incomes().isEmpty());

        assertEquals(1234, newBudget.totalIncome());

        assertEquals(1234, newBudget.currentTotal());

        webTestClient.patch()
                .uri("/budgets/{budgetId}/remove-income/{incomeId}", budgetId, newBudget.incomes().getFirst().id())
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        BudgetResponse removedBudget = getBudget();

        assertNotNull(removedBudget);
        assertTrue(removedBudget.incomes().isEmpty());

        assertEquals(0, removedBudget.totalIncome());

        assertEquals(0, removedBudget.currentTotal());
    }

    @Test
    void canAddExpense() {
        BudgetResponse budget = getBudget();

        assertNotNull(budget);

        createExpense();

        BudgetResponse newBudget = getBudget();

        assertNotNull(newBudget);

        assertFalse(newBudget.expenses().isEmpty());

        assertEquals(1234, newBudget.totalExpense());
        assertEquals(-1234, newBudget.currentTotal());
    }

    @Test
    void canRemoveExpense() {
        BudgetResponse budget = getBudget();

        assertNotNull(budget);

        createExpense();

        BudgetResponse newBudget = getBudget();

        assertNotNull(newBudget);

        assertFalse(newBudget.expenses().isEmpty());

        assertEquals(1234, newBudget.totalExpense());
        assertEquals(-1234, newBudget.currentTotal());

        webTestClient.patch()
                .uri("/budgets/{budgetId}/remove-expense/{expenseId}", budgetId, newBudget.expenses().getFirst().id())
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        BudgetResponse removedBudget = getBudget();

        assertNotNull(removedBudget);
        assertTrue(removedBudget.expenses().isEmpty());

        assertEquals(0, removedBudget.totalExpense());
        assertEquals(0, removedBudget.currentTotal());
    }

    @Test
    void canDeleteBudget() {
        BudgetResponse budget = getBudget();

        assertNotNull(budget);

        webTestClient.delete()
                .uri("/budgets/{id}", budgetId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/budgets/{id}", budgetId)
                .header("Authorization", token)
                .exchange()
                .expectStatus().isNotFound();
    }

    private BudgetResponse getBudget() {
        return webTestClient.get()
                .uri("/budgets/{id}", budgetId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BudgetResponse.class)
                .returnResult()
                .getResponseBody();
    }

    private void createExpense() {

        ExpenseCategoryRequest categoryRequest = new ExpenseCategoryRequest("category name", "category description");

        Long catId = webTestClient.post()
                .uri("/expense-categories")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(categoryRequest), ExpenseCategoryRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .returnResult().getResponseBody();

        CreateExpenseRequest expenseRequest = new CreateExpenseRequest(1234, Instant.now(), null, catId);

        webTestClient.patch()
                .uri("/budgets/{budgetId}/add-expense", budgetId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(expenseRequest), CreateExpenseRequest.class)
                .exchange()
                .expectStatus().isOk();
    }
}
