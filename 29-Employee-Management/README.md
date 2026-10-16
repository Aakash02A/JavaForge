# 29 - Employee Management

Build an **Employee Management API** using Spring Data JPA so employee records are stored in a relational database and exposed through a clean REST interface.

# Problem Statement

Employee records are usually business-critical and need to be persistent, searchable, and validated. A simple list in memory is not enough for real systems. This project introduces database-backed persistence with JPA and H2, making it a natural step from in-memory CRUD applications into realistic backend development.

# Objectives

- Use Spring Data JPA for employee persistence.
- Create a database-backed employee resource.
- Build CRUD APIs with validation rules.
- Enforce unique email constraints.
- Learn how JPA entities map to application endpoints.

# Features

- Create employee records.
- List all employees.
- Fetch one employee by ID.
- Update employee details.
- Delete an employee.
- Validate salary, hire date, department, and job title data.
- Prevent duplicate emails.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate ORM
- H2 in-memory database
- Maven

# Architecture and Flow

The project uses a standard database-backed pattern:

- Controller receives employee requests.
- Service keeps business logic and validation rules.
- Repository handles database queries with Spring Data JPA.
- Entity model maps to the `employees` table.
- DTOs help keep API payloads clean and predictable.

Because the app uses H2, there is no need for a local database server. Data resets when the application restarts.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The API runs on:

```text
http://localhost:8080
```

The H2 console is also available at:

```text
http://localhost:8080/h2-console
```

Use the JDBC URL:

```text
jdbc:h2:mem:employees
```

with username `sa` and empty password.

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/employees` | Create an employee | `201 Created` |
| `GET` | `/api/employees` | List all employees | `200 OK` |
| `GET` | `/api/employees/{id}` | Get one employee | `200 OK` |
| `PUT` | `/api/employees/{id}` | Update an employee | `200 OK` |
| `DELETE` | `/api/employees/{id}` | Delete an employee | `204 No Content` |

Validation rules:

- First name and last name are required.
- Email must be valid and unique.
- Department and job title are required.
- Salary must be greater than zero.
- Hire date cannot be in the future.

Business rules:

- Duplicate emails return `409 Conflict`.
- Missing employees return `404 Not Found`.
- Invalid requests return `400 Bad Request`.

# Example Requests

```bash
curl -i -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Maya","lastName":"Rao","email":"maya@example.com","department":"Engineering","jobTitle":"Software Engineer","salary":95000.00,"hireDate":"2022-04-15"}'

curl http://localhost:8080/api/employees

curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Maya","lastName":"Shah","email":"maya@example.com","department":"Engineering","jobTitle":"Senior Engineer","salary":110000.00,"hireDate":"2022-04-15"}'

curl -i http://localhost:8080/api/employees/999
curl -i -X DELETE http://localhost:8080/api/employees/1
```

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 3–5 hours

Skills Gained:
- Spring Data JPA basics
- Relational persistence in Java
- Entity validation and constraints
- Database-backed REST services
- H2 database testing and development

## Tests

Run `mvn test` to execute `EmployeeManagementIntegrationTest`. The integration test exercises HTTP operations and JPA persistence against the configured in-memory H2 database, so a separate database server is not needed.
