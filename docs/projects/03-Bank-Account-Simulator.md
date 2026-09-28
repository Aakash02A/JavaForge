# Project Idea

Build a **Bank Account Simulator** using Inheritance to manage different account types from the console.

# Problem Statement

Banks need to handle different types of accounts (Savings, Current) with varying rules for withdrawals, overdrafts, and interest. The **Bank Account Simulator** is a Java console application that models real-world banking operations using OOP inheritance.

# Objectives

- Demonstrate Inheritance through abstract base class and concrete subclasses.
- Create and manage Savings and Current accounts.
- Perform deposits, withdrawals, and fund transfers.
- Enforce account-type-specific business rules (minimum balance, overdraft limits).
- Track and display transaction history.
- Apply interest to savings accounts.

# Features

- Create Account (Savings / Current)
- Deposit
- Withdraw (with validation per account type)
- Transfer Between Accounts
- Check Balance
- View All Accounts
- Transaction History
- Apply Interest (Savings Accounts)
- Exit

# Account Details

- Account Number
- Account Holder Name
- Balance
- Account Type (Savings / Current)
- Interest Rate (Savings only)
- Minimum Balance (Savings only)
- Overdraft Limit (Current only)
- Transaction History (timestamped log)

# Tech Stack

- Java (Core Java 21)
- OOP (Inheritance, Abstraction, Polymorphism, Encapsulation)
- Collections (ArrayList)
- Java Time API (LocalDateTime)
- Scanner (User Input)

# Class Hierarchy

```
BankAccount (abstract)
   ├── SavingsAccount
   └── CurrentAccount
```

# Learning Outcomes

By completing this project, you will learn:
- Designing class hierarchies with abstract classes
- Method overriding for polymorphic behavior
- Enforcing business rules through inheritance
- State management with transaction logging
- Working with Java Time API for timestamps

# Project Details

Difficulty: ⭐⭐☆☆☆ (Beginner-Intermediate)
Estimated Time: 6–10 hours
Skills Gained: Core Java, OOP Inheritance, Collections, Console Application
