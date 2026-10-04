# Todo REST API

A small, layered Todo API built with Java 21 and Spring Boot 3. Todos are held
in thread-safe in-memory storage and are cleared whenever the application stops.
This project focuses on HTTP and REST fundamentals; database persistence is
introduced in later projects.

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
| `POST` | `/api/todos` | Create a todo | `201 Created` with `Location` |
| `GET` | `/api/todos` | List todos in creation order | `200 OK` |
| `GET` | `/api/todos/{id}` | Get one todo | `200 OK` |
| `PUT` | `/api/todos/{id}` | Replace title, description, and completion state | `200 OK` |
| `PATCH` | `/api/todos/{id}/completion` | Change completion state | `200 OK` |
| `DELETE` | `/api/todos/{id}` | Delete a todo | `204 No Content` |

Titles are required, must contain non-whitespace text, and may be at most 120
characters. Descriptions may be up to 2,000 characters. Missing resources return
`404`; invalid request bodies return `400` with field-level validation errors.

## Example

```bash
curl -i -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Prepare demo","description":"Show the Todo API"}'
curl http://localhost:8080/api/todos
curl -X PATCH http://localhost:8080/api/todos/1/completion \
  -H "Content-Type: application/json" \
  -d '{"completed":true}'
curl -i http://localhost:8080/api/todos/999
```

## Test

```bash
mvn test
```

The tests cover the service lifecycle and HTTP status codes, response bodies,
validation, and not-found handling.
