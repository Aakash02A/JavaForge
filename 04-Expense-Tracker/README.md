# 04 - Expense Tracker

Build an **Expense Tracker** that allows users to manage their daily expenses and track spending from the console.

# Problem Statement

Many individuals struggle to keep track of their daily expenses, leading to poor financial management.

The **Expense Tracker** is a Java console application that provides an easy way to manage expenses digitally. It enables users to add, view, and categorize expenses, reducing manual effort and improving financial organization.

# Objectives

- Store expense information.
- Add new expenses.
- View all expenses.
- Search for an expense by category.
- Delete an expense.

# Features

- Add Expense
- View All Expenses
- View Expenses by Category
- Calculate Total Expenses
- Delete Expense
- Exit

# Expense Details

- Expense ID
- Description
- Amount
- Category
- Date

# Tech Stack

- Java (Core Java)
- OOP (Classes & Objects)
- Array / ArrayList
- Scanner (User Input)
- Loops & Conditional Statements
- Methods

# Suggested Class Structure

Expense
---------
id
description
amount
category
date

ExpenseTracker
-----------------
addExpense()
viewExpenses()
viewByCategory()
calculateTotal()
deleteExpense()

Main
----
menu()
main()

# Sample Menu

========== Expense Tracker ==========

1. Add Expense
2. View Expenses
3. View by Category
4. Calculate Total
5. Delete Expense
6. Exit

Enter your choice:

# Learning Outcomes

By completing this project, you will learn:

- Creating and using classes and objects
- Constructors and methods
- Encapsulation (getters and setters)
- Working with ArrayList
- Using loops and conditional statements
- Implementing CRUD operations
- Building a menu-driven console application

# Project Details

Difficulty: ⭐⭐☆☆☆ (Beginner)

Estimated Time: 4–8 hours

Skills Gained:
- Core Java
- OOP
- Collections
- Console-based application development

## Build and run

Requirements: JDK 21 and Maven.

From this directory:

```bash
mvn clean package
```

Run the `Main` class from the IDE to interact with the menu. The application stores expenses in memory, so its data resets when it exits.

## Tests

Run `mvn test`. The project has a service-level test; use the console menu to verify add, list, category search, totals, and delete behavior.
