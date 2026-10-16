# 26 - Book Store API

Build a **Book Store API** that manages a catalog of books with layered architecture, validation, and business rules for stock and ISBN uniqueness.

# Problem Statement

Online and offline bookstores need a consistent way to manage inventory, prices, and product identity. Without validation, duplicate ISBNs and invalid stock values can create confusion and inaccurate inventory records. This project adds a clean service architecture and disciplined data rules.

# Objectives

- Build a layered backend for a bookstore catalog.
- Manage book creation, retrieval, update, and deletion.
- Validate title, author, ISBN, stock, and price values.
- Ensure each book has a unique normalized ISBN.
- Practice clean separation between controller, service, and repository logic.

# Features

- Add books to the catalog.
- View all books in the store.
- Fetch a book by ID.
- Update book details.
- Delete book records.
- Reject negative stock and invalid prices.
- Prevent duplicate ISBN values after normalization.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Bean Validation
- Maven
- In-memory catalog storage

# Architecture and Flow

This project is the first in the sequence to demonstrate a clearer layered structure:

- Controller handles HTTP endpoints.
- Service validates and enforces business rules.
- Repository stores catalog objects in memory.
- Model layer defines the domain entity.
- Request DTOs keep the request payloads precise and safe.

The design makes the project easier to scale when later tasks add database-backed persistence or more complex product features.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The API is available at:

```text
http://localhost:8080
```

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/books` | Add a book | `201 Created` |
| `GET` | `/api/books` | List all books | `200 OK` |
| `GET` | `/api/books/{id}` | Get one book | `200 OK` |
| `PUT` | `/api/books/{id}` | Update a book | `200 OK` |
| `DELETE` | `/api/books/{id}` | Delete a book | `204 No Content` |

Validation rules:

- Title is required and limited to 200 characters.
- Author is required and limited to 120 characters.
- ISBN is required and limited to 20 characters.
- Price must be greater than zero and have at most two decimal places.
- Stock cannot be negative.
- ISBN values are normalized by stripping spaces and hyphens and converting to uppercase.

Business rules:

- Duplicate ISBN values return `409 Conflict`.
- Invalid requests return `400 Bad Request`.
- Missing books return `404 Not Found`.

# Example Requests

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

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 3–5 hours

Skills Gained:
- Layered backend design
- Validation and normalization
- Business rule enforcement
- Clean REST resource modeling
- Structured error handling

## Tests

Run `mvn test` to execute `BookControllerTest` and `BookServiceTest`. Controller tests verify HTTP behavior and service tests cover book operations and business rules. No external database server is required.
