# Project Idea

Build a **Banking System** that manages customer accounts and supports deposits, withdrawals, and fund transfers using JDBC transactions.

# Problem Statement

Modern banking applications must provide reliable financial operations while ensuring data consistency. This project focuses on transactional database logic so that account updates are atomic and safe.

# Objectives

- Connect a Java application to a relational database using JDBC.
- Create and manage customer accounts.
- Perform deposits and withdrawals securely.
- Transfer funds between accounts with transaction safety.
- Validate insufficient-funds scenarios and rollback failed updates.

# Features

- Create new bank accounts.
- View account balances.
- Deposit money into an account.
- Withdraw money from an account when sufficient balance exists.
- Transfer money between accounts using database transactions.
- Prevent invalid operations such as overdrafts and self-transfers.

# Tech Stack

- Java (Core Java, JDBC)
- Relational Database (H2 Database used for tests, swappable with MySQL/PostgreSQL)
- JUnit 5 for Testing
- Transaction Management / DAO Pattern

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 3–5 hours
