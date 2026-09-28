package com.javaforge.expense.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.javaforge.expense.model.Category;
import com.javaforge.expense.model.ExpenseEntry;

public class ExpenseTracker {
    private final List<ExpenseEntry> transactions = new ArrayList<>();
    private int nextId = 1;

    public ExpenseTracker() {
    }

    public void addTransaction(String title, Category category, double amount, LocalDate date, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (date == null) {
            date = LocalDate.now();
        }

        transactions.add(new ExpenseEntry(nextId++, title.trim(), category, amount, date, description));
    }

    public List<ExpenseEntry> getAllTransactions() {
        return transactions.stream()
                .sorted(Comparator.comparing(ExpenseEntry::getDate).reversed())
                .collect(Collectors.toList());
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(ExpenseEntry::isExpense)
                .mapToDouble(ExpenseEntry::getAmount)
                .sum();
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(ExpenseEntry::isIncome)
                .mapToDouble(ExpenseEntry::getAmount)
                .sum();
    }

    public double getNetBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    public void viewAllTransactions() {
        List<ExpenseEntry> entries = getAllTransactions();
        if (entries.isEmpty()) {
            System.out.println("No transactions available yet.");
            return;
        }

        System.out.println("===== Transaction History =====");
        for (ExpenseEntry entry : entries) {
            System.out.println(entry);
        }
    }

    public void viewSummary() {
        System.out.println("===== Expense Summary =====");
        System.out.println("Total Income: $" + String.format("%.2f", getTotalIncome()));
        System.out.println("Total Expense: $" + String.format("%.2f", getTotalExpense()));
        System.out.println("Net Balance: $" + String.format("%.2f", getNetBalance()));
    }

    public void viewCategoryBreakdown() {
        Map<Category, Double> totals = new TreeMap<>();

        for (Category category : Category.values()) {
            totals.put(category, 0.0);
        }

        for (ExpenseEntry entry : transactions) {
            double current = totals.getOrDefault(entry.getCategory(), 0.0);
            totals.put(entry.getCategory(), current + entry.getAmount());
        }

        System.out.println("===== Category Breakdown =====");
        boolean found = false;
        for (Map.Entry<Category, Double> entry : totals.entrySet()) {
            if (entry.getValue() > 0) {
                found = true;
                System.out.println(entry.getKey().getDisplayName() + ": $" + String.format("%.2f", entry.getValue()));
            }
        }

        if (!found) {
            System.out.println("No category data available.");
        }
    }

    public boolean removeTransaction(int id) {
        return transactions.removeIf(entry -> entry.getId() == id);
    }
}
