# Project Idea

Build an **Expense Tracker Database** application that stores, retrieves, updates, and deletes expense records using JDBC.

# Problem Statement

Expense data should persist across application runs and be easy to maintain. This project practices JDBC CRUD operations by storing descriptions, categories, amounts, and dates in a relational database.

# Objectives

- Connect Java to a relational database using JDBC.
- Create a relational table for expense records.
- Implement create, read, update, and delete operations.
- Represent monetary amounts with decimal precision.
- Validate expense data before writing it to the database.

# Features

- Add an expense with a description, category, amount, and date.
- Retrieve an expense by ID.
- List expenses ordered by date.
- Update an existing expense.
- Delete an expense by ID.

# Tech Stack

- Java (Core Java, JDBC)
- MySQL 8.0+ with MySQL Connector/J
- H2 Database for isolated automated tests
- JUnit 5 for Testing
- DAO Pattern / SQL CRUD

# MySQL Setup

1. Run `database/schema.sql` using the MySQL client to create the database and `expenses` table.
2. Copy `database/database.properties.example` to `database/database.properties`, then set your local MySQL username and password in the copied file. The actual `.properties` file is ignored by Git.
3. Create the DAO from the local configuration:

```java
ExpenseDAO dao = ExpenseDAO.fromLocalConfig();
```

The JDBC DAO uses prepared statements for its CRUD operations. Example setup and `INSERT`, `SELECT`, `UPDATE`, and `DELETE` queries are included in `database/schema.sql`. Do not commit real credentials or paste them into source code.

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 3–5 hours
