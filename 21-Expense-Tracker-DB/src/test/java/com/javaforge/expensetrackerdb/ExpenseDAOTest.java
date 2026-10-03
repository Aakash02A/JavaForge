package com.javaforge.expensetrackerdb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseDAOTest {
    private final String url = "jdbc:h2:mem:expensedb;DB_CLOSE_DELAY=-1";
    private ExpenseDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ExpenseDAO(url, "sa", "");
        dao.createTable();
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DriverManager.getConnection(url, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE expenses");
        }
    }

    @Test
    void insertsAndReadsExpense() {
        Expense expense = new Expense("  Weekly groceries  ", "Food",
                new BigDecimal("54.25"), LocalDate.of(2026, 10, 1));
        dao.insert(expense);

        assertTrue(expense.getId() > 0);
        Expense found = dao.findById(expense.getId());
        assertNotNull(found);
        assertEquals("Weekly groceries", found.getDescription());
        assertEquals("Food", found.getCategory());
        assertEquals(new BigDecimal("54.25"), found.getAmount());
        assertEquals(LocalDate.of(2026, 10, 1), found.getExpenseDate());
    }

    @Test
    void listsExpensesInDateOrder() {
        dao.insert(new Expense("Older", "Travel", new BigDecimal("10.00"), LocalDate.of(2026, 9, 1)));
        dao.insert(new Expense("Newer", "Food", new BigDecimal("20.00"), LocalDate.of(2026, 10, 1)));

        List<Expense> expenses = dao.findAll();
        assertEquals(2, expenses.size());
        assertEquals("Newer", expenses.get(0).getDescription());
        assertEquals("Older", expenses.get(1).getDescription());
    }

    @Test
    void updatesExpense() {
        Expense expense = new Expense("Lunch", "Food", new BigDecimal("12.00"), LocalDate.of(2026, 10, 2));
        dao.insert(expense);
        expense.setDescription("Team lunch");
        expense.setAmount(new BigDecimal("38.75"));
        expense.setCategory("Dining");

        assertTrue(dao.update(expense));
        Expense updated = dao.findById(expense.getId());
        assertEquals("Team lunch", updated.getDescription());
        assertEquals("Dining", updated.getCategory());
        assertEquals(new BigDecimal("38.75"), updated.getAmount());
        assertFalse(dao.update(new Expense(999, "Missing", "Other",
                new BigDecimal("1.00"), LocalDate.of(2026, 10, 2))));
    }

    @Test
    void deletesExpense() {
        Expense expense = new Expense("Taxi", "Travel", new BigDecimal("8.50"), LocalDate.of(2026, 10, 3));
        dao.insert(expense);

        assertTrue(dao.delete(expense.getId()));
        assertNull(dao.findById(expense.getId()));
        assertFalse(dao.delete(expense.getId()));
    }

    @Test
    void rejectsInvalidAmount() {
        Expense invalid = new Expense("Invalid", "Other", new BigDecimal("-1.00"), LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> dao.insert(invalid));
    }
}
