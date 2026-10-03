package com.javaforge.expensetrackerdb;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Expense {
    private int id;
    private String description;
    private String category;
    private BigDecimal amount;
    private LocalDate expenseDate;

    public Expense(String description, String category, BigDecimal amount, LocalDate expenseDate) {
        this(0, description, category, amount, expenseDate);
    }

    public Expense(int id, String description, String category, BigDecimal amount, LocalDate expenseDate) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Expense expense = (Expense) other;
        return id == expense.id &&
                Objects.equals(description, expense.description) &&
                Objects.equals(category, expense.category) &&
                Objects.equals(amount, expense.amount) &&
                Objects.equals(expenseDate, expense.expenseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, category, amount, expenseDate);
    }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", description='" + description + "', category='" + category +
                "', amount=" + amount + ", expenseDate=" + expenseDate + "}";
    }
}
