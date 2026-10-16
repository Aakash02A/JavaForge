# 19 - Hospital Management

Build a **Hospital Management** application that stores patient information in a database and supports CRUD-style hospital operations through JDBC.

# Problem Statement

Healthcare systems need reliable storage and retrieval of patient details, department assignments, and treatment statuses. This project teaches how to model a real-world hospital workflow using relational databases and Java.

# Objectives

- Connect Java applications to a database using JDBC.
- Create a relational schema for patient records.
- Insert, view, update, and delete patient information.
- Query records by department and patient status.
- Use DAO-based design for cleaner data access.

# Features

- Add new patient records.
- Retrieve a single patient by ID.
- View all patients registered in the system.
- Filter patients by department.
- Update the patient treatment status.
- Delete patient records when necessary.

# Tech Stack

- Java (Core Java, JDBC)
- Relational Database (H2 Database used for tests, swappable with MySQL/PostgreSQL)
- JUnit 5 for Testing
- DAO Pattern / SQL

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 3–5 hours

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

The `HospitalDAO` tests create an isolated H2 database and exercise patient insertion, lookup, listing, department filtering, status updates, and deletion. You do not need to install MySQL or start an external database.

## Data model and query practice

`Patient` maps the patient fields used by this project (including department, doctor, and treatment status). `HospitalDAO` contains the SQL and row-to-object mapping. The filter and update operations show how to parameterize queries and return whether a matching row was changed.

Use the test cases as small runnable examples: create the schema, insert sample patients, search by ID or department, update a patient's status, then check the updated row. This is a JDBC persistence module, not a deployed hospital system or HTTP API.

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

The automated tests use an isolated in-memory H2 database and exercise the `HospitalDAO` patient operations; MySQL is not required. The project is a JDBC persistence example, not a running web application.
