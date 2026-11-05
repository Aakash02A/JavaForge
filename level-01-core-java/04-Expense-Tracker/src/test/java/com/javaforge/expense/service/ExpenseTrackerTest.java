package com.javaforge.expense.service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.javaforge.expense.model.Category;

class ExpenseTrackerTest {
    @Test
    void shouldCalculateIncomeExpenseAndNetBalance() {
        ExpenseTracker tracker = new ExpenseTracker();

        tracker.addTransaction("Salary", Category.SALARY, 3500.00, LocalDate.of(2026, 9, 1), "Monthly salary");
        tracker.addTransaction("Rent", Category.RENT, 1200.00, LocalDate.of(2026, 9, 2), "Apartment rent");
        tracker.addTransaction("Groceries", Category.FOOD, 220.00, LocalDate.of(2026, 9, 3), "Food & dining");

        assertEquals(1420.00, tracker.getTotalExpense(), 0.01);
        assertEquals(3500.00, tracker.getTotalIncome(), 0.01);
        assertEquals(2080.00, tracker.getNetBalance(), 0.01);
    }
}
