# 18 - Banking System

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

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

The tests use an in-memory H2 database and cover account persistence, deposits, withdrawals, and transfers. No separate database server is required. This project is a JDBC DAO example; it does not include a console UI or REST API.

## How the transfer works

`BankAccountDAO.transfer(fromId, toId, amount)` opens one JDBC connection, disables auto-commit, validates the source and destination, applies the debit and credit, and commits only if both updates succeed. On a failure it rolls the transaction back so the two balances cannot be left half-updated.

Try a successful transfer, an amount larger than the source balance, a missing account, and a same-account transfer. Verify both account balances after each attempt. This example stores money as `double` and is educational only; production financial systems need decimal arithmetic, stronger concurrency controls, and audited persistence.

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

The tests exercise JDBC account operations against an in-memory H2 database. They cover account setup and balance changes, including transfer behavior and invalid operations. No database server is needed. This project provides a `BankAccountDAO` rather than a standalone UI or REST service.
