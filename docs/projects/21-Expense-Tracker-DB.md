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
2. Connect the DAO to the local MySQL database:

```java
ExpenseDAO dao = new ExpenseDAO(
        "jdbc:mysql://localhost:3306/expense_tracker",
        "your_mysql_user",
        "your_mysql_password"
);
```

The JDBC DAO uses prepared statements for its CRUD operations. Example setup and `INSERT`, `SELECT`, `UPDATE`, and `DELETE` queries are included in `database/schema.sql`.

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 3–5 hours
