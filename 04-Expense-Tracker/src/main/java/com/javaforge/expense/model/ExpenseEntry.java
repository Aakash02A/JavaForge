package com.javaforge.expense.model;

import java.time.LocalDate;

public class ExpenseEntry {
    private final int id;
    private final String title;
    private final Category category;
    private final double amount;
    private final LocalDate date;
    private final String description;

    public ExpenseEntry(int id, String title, Category category, double amount, LocalDate date, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Category getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIncome() {
        return category == Category.SALARY || category == Category.FREELANCE;
    }

    public boolean isExpense() {
        return !isIncome();
    }

    @Override
    public String toString() {
        return "ID: " + id +
                " | Title: " + title +
                " | Category: " + category.getDisplayName() +
                " | Amount: $" + String.format("%.2f", amount) +
                " | Date: " + date +
                (description == null || description.isBlank() ? "" : " | Note: " + description);
    }
}
