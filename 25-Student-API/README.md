# Student API

A layered Student CRUD API built with Java 21 and Spring Boot 3. Student
records are held in thread-safe in-memory storage and are cleared when the
application stops. This project focuses on request validation; database
persistence is covered by the later Spring Data JPA projects.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`.

## Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/students` | Create a student | `201 Created` with `Location` |
| `GET` | `/api/students` | List students in creation order | `200 OK` |
| `GET` | `/api/students/{id}` | Get one student | `200 OK` |
| `PUT` | `/api/students/{id}` | Replace a student's details | `200 OK` |
| `DELETE` | `/api/students/{id}` | Delete a student | `204 No Content` |

## Validation

- `fullName`: required, non-blank, at most 100 characters
- `email`: required, valid email address, at most 254 characters
- `age`: between 16 and 120 inclusive
- `course`: required, non-blank, at most 100 characters

Emails are trimmed, normalized to lowercase, and unique. Invalid requests return
`400 Bad Request` with field-level errors; a duplicate email returns `409
Conflict`; an unknown student ID returns `404 Not Found`.

## Example

```bash
curl -i -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Asha Rao","email":"asha@example.com","age":20,"course":"Computer Science"}'
curl http://localhost:8080/api/students
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Asha Patel","email":"asha@example.com","age":21,"course":"Data Science"}'
curl -i http://localhost:8080/api/students/999
curl -i -X DELETE http://localhost:8080/api/students/1
```

## Test

```bash
mvn test
mvn package
```

Tests cover student CRUD, normalization, duplicate email handling, field
validation, and HTTP error responses.
