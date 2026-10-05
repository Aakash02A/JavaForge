# Employee Management

A Java 21 / Spring Boot 3 REST API demonstrating Spring Data JPA with an
Employee entity and repository. It uses an H2 in-memory database by default,
so it runs without a database server; data resets when the application stops.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`. The H2 console is available at
`http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:employees`,
username `sa`, and an empty password.

## Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/employees` | Create an employee | `201 Created` with `Location` |
| `GET` | `/api/employees` | List employees by ID | `200 OK` |
| `GET` | `/api/employees/{id}` | Get an employee | `200 OK` |
| `PUT` | `/api/employees/{id}` | Replace employee details | `200 OK` |
| `DELETE` | `/api/employees/{id}` | Delete an employee | `204 No Content` |

Employee requests require first name, last name, valid unique email, department,
job title, salary greater than zero (up to two decimal places), and a hire date
that is not in the future. Emails are normalized to lowercase. Invalid requests
return `400`, duplicate emails return `409`, and missing employees return `404`
with a structured error response.

## Example

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

## Test

```bash
mvn test
mvn package
```

Integration tests use the configured H2 database and cover persistence, CRUD,
field validation, duplicate email protection, and not-found responses.
