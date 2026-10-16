# 17 - Employee CRUD

Build an **Employee CRUD Application** that connects to a database to Create, Read, Update, and Delete employee records.

# Problem Statement

Many applications require persisting data across sessions. Learning how to interface Java with a relational database is a core skill for any backend developer.

The **Employee CRUD** project is a Java application demonstrating how to use JDBC (Java Database Connectivity) to perform essential database operations on a simple `employees` table.

# Objectives

- Connect Java to a Relational Database using JDBC.
- Perform Data Definition operations (CREATE TABLE).
- Perform Data Manipulation operations (INSERT, SELECT, UPDATE, DELETE).
- Map Database Rows to Java Objects (DAO Pattern).

# Features

- Insert new employee records.
- Retrieve a single employee by ID.
- Retrieve a list of all employees.
- Update an existing employee's details.
- Delete an employee by ID.

# Tech Stack

- Java (Core Java, JDBC)
- Relational Database (H2 Database used for tests/demonstration, easily swappable with MySQL/PostgreSQL)
- JUnit 5 for Testing
- Data Access Object (DAO) Pattern

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 3–5 hours

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

The DAO tests use an in-memory H2 database, so no MySQL server or credentials are needed to run the automated suite. This project exposes persistence operations through `EmployeeDAO`; it does not include a web API or console `Main` class. Use the DAO tests as executable examples of CRUD usage.

## How to explore

Construct an `EmployeeDAO` with a JDBC URL, username, and password, call `createTable()`, then use `insert`, `findById`, `findAll`, `update`, and `delete`. For H2, use an in-memory URL such as `jdbc:h2:mem:employees;DB_CLOSE_DELAY=-1`; the `DB_CLOSE_DELAY` option keeps the database alive while the test opens and closes connections. `Employee` is the row-mapping model.

The tests cover table setup, insert/read, update, and delete. The DAO uses prepared statements, but it prints SQL exceptions and returns sentinel values in some methods; inspect those error paths as part of the JDBC lesson before reusing this design in a larger application.
