package com.javaforge.expense;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.javaforge.expense.model.Category;
import com.javaforge.expense.service.ExpenseTracker;

public class Main {
    private static final ExpenseTracker tracker = new ExpenseTracker();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n===== Expense Tracker =====");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View All Transactions");
            System.out.println("4. View Summary");
            System.out.println("5. View Category Breakdown");
            System.out.println("6. Remove Transaction");
            System.out.println("7. Exit");
            System.out.print("Enter Choice: ");

            try {
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addIncome();
                    case 2 -> addExpense();
                    case 3 -> tracker.viewAllTransactions();
                    case 4 -> tracker.viewSummary();
                    case 5 -> tracker.viewCategoryBreakdown();
                    case 6 -> removeTransaction();
                    case 7 -> {
                        System.out.println("Exiting Expense Tracker. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addIncome() {
        System.out.println("===== Add Income =====");
        String title = readString("Enter title: ");
        Category category = readCategory("Select category:");
        double amount = readAmount("Enter amount: ");
        LocalDate date = readDate("Enter date (yyyy-mm-dd, leave blank for today): ");
        String description = readStringOptional("Enter description (optional): ");

        tracker.addTransaction(title, category, amount, date, description);
        System.out.println("Income added successfully.");
    }

    private static void addExpense() {
        System.out.println("===== Add Expense =====");
        String title = readString("Enter title: ");
        Category category = readExpenseCategory("Select category:");
        double amount = readAmount("Enter amount: ");
        LocalDate date = readDate("Enter date (yyyy-mm-dd, leave blank for today): ");
        String description = readStringOptional("Enter description (optional): ");

        tracker.addTransaction(title, category, amount, date, description);
        System.out.println("Expense added successfully.");
    }

    private static void removeTransaction() {
        System.out.print("Enter transaction ID to remove: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (tracker.removeTransaction(id)) {
            System.out.println("Transaction removed successfully.");
        } else {
            System.out.println("Transaction with ID " + id + " not found.");
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static String readStringOptional(String prompt) {
        System.out.print(prompt);
        String value = sc.nextLine().trim();
        return value.isEmpty() ? null : value;
    }

    private static double readAmount(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double amount = Double.parseDouble(sc.nextLine());
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be greater than 0.");
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid numeric amount.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            return LocalDate.now();
        }

        try {
            return LocalDate.parse(input);
        } catch (Exception e) {
            System.out.println("Invalid date format. Using today's date instead.");
            return LocalDate.now();
        }
    }

    private static Category readCategory(String prompt) {
        System.out.println(prompt);
        for (int i = 0; i < Category.values().length; i++) {
            System.out.println((i + 1) + ". " + Category.values()[i].getDisplayName());
        }
        System.out.print("Enter option: ");

        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice >= 1 && choice <= Category.values().length) {
                    return Category.values()[choice - 1];
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Invalid category selection. Try again.");
            System.out.print("Enter option: ");
        }
    }

    private static Category readExpenseCategory(String prompt) {
        System.out.println(prompt);
        Category[] expenseCategories = {
                Category.FOOD,
                Category.TRANSPORT,
                Category.ENTERTAINMENT,
                Category.UTILITIES,
                Category.SHOPPING,
                Category.HEALTH,
                Category.EDUCATION,
                Category.RENT,
                Category.OTHER
        };

        for (int i = 0; i < expenseCategories.length; i++) {
            System.out.println((i + 1) + ". " + expenseCategories[i].getDisplayName());
        }
        System.out.print("Enter option: ");

        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice >= 1 && choice <= expenseCategories.length) {
                    return expenseCategories[choice - 1];
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Invalid category selection. Try again.");
            System.out.print("Enter option: ");
        }
    }
}
