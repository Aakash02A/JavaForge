# Book Store API

A layered Book Store CRUD API built with Java 21 and Spring Boot 3. It uses
separate controller, service, repository, model, and request DTO layers.
Books are stored in thread-safe in-memory storage and are cleared when the
application stops; database persistence is introduced by later JPA projects.

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
| `POST` | `/api/books` | Add a book | `201 Created` with `Location` |
| `GET` | `/api/books` | List books in creation order | `200 OK` |
| `GET` | `/api/books/{id}` | Get one book | `200 OK` |
| `PUT` | `/api/books/{id}` | Replace a book's details | `200 OK` |
| `DELETE` | `/api/books/{id}` | Remove a book | `204 No Content` |

Titles (up to 200 characters), authors (up to 120 characters), and ISBN values
(up to 20 characters) are required. Prices must be greater than zero with at
most two decimal places; stock cannot be negative. ISBN values are normalized
by removing spaces and hyphens and converting to uppercase, and must be unique.
Invalid requests return `400`, duplicate ISBNs return `409`, and missing books
return `404`, all with structured error responses.

## Example

```bash
curl -i -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert Martin","isbn":"978-0-13-235088-4","price":39.99,"stock":8}'
curl http://localhost:8080/api/books
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Coder","author":"Robert Martin","isbn":"978-0-13-235088-4","price":44.50,"stock":5}'
curl -i http://localhost:8080/api/books/999
curl -i -X DELETE http://localhost:8080/api/books/1
```

## Test

```bash
mvn test
mvn package
```

Tests cover the service and HTTP layers, CRUD behavior, input validation,
duplicate ISBN handling, and not-found responses.
