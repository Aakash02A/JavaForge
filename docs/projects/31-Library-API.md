# Project Idea

Build a **Library API** that models a one-to-many relationship between libraries and books, including nested creation, updates, and deletion behavior.

# Problem Statement

Libraries often contain many books, and the relationship between the parent library and its books is critical. When a library is deleted, its books may also need to be removed. Similarly, adding or updating books must stay attached to the right library. This project teaches parent-child persistence in a real JPA model.

# Objectives

- Model a `Library` entity with many `Book` entities.
- Persist the relationship using Spring Data JPA.
- Add nested book management under each library.
- Use cascade behavior and orphan removal carefully.
- Build a realistic REST API around a parent-child domain model.

# Features

- Create libraries.
- Get libraries and their books.
- Add books to a library.
- Update books and library details.
- Remove a book from a library.
- Delete a library and its child books.
- Validate publication year and required child fields.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate ORM
- H2 in-memory database
- Maven

# Architecture and Flow

This project demonstrates a classic one-to-many design:

- One library owns many books.
- Each book belongs to exactly one library.
- Parent-child persistence is handled by JPA.
- DTOs return compact nested data without exposing raw JPA entities.

The project also highlights the need to manage cascading rules properly. Deleting a library should not leave orphan book rows behind, and updating the child collection must remain consistent.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The app runs at:

```text
http://localhost:8080
```

The H2 database console is available at:

```text
http://localhost:8080/h2-console
```

with JDBC URL:

```text
jdbc:h2:mem:library
```

and username `sa`.

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/libraries` | Create a library | `201 Created` |
| `GET` | `/api/libraries` | List libraries and books | `200 OK` |
| `GET` | `/api/libraries/{libraryId}` | Get one library and its books | `200 OK` |
| `PUT` | `/api/libraries/{libraryId}` | Update a library | `200 OK` |
| `POST` | `/api/libraries/{libraryId}/books` | Add a book to a library | `201 Created` |
| `GET` | `/api/libraries/{libraryId}/books/{bookId}` | Get a specific library book | `200 OK` |
| `PUT` | `/api/libraries/{libraryId}/books/{bookId}` | Update a specific book | `200 OK` |
| `DELETE` | `/api/libraries/{libraryId}/books/{bookId}` | Remove a book | `204 No Content` |
| `DELETE` | `/api/libraries/{libraryId}` | Delete a library and its books | `204 No Content` |

Validation rules include:

- Library name and address are required.
- Book title, author, and ISBN are required.
- Publication year must be between 1450 and 2100.
- Unknown library or book IDs return `404 Not Found`.
- Invalid payloads return `400 Bad Request`.

# Example Requests

```bash
curl -i -X POST http://localhost:8080/api/libraries \
  -H "Content-Type: application/json" \
  -d '{"name":"Central Library","address":"10 Main Street"}'

curl -i -X POST http://localhost:8080/api/libraries/1/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert Martin","isbn":"978-0-13-235088-4","publicationYear":2008}'

curl http://localhost:8080/api/libraries/1
curl -i -X DELETE http://localhost:8080/api/libraries/1
```

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 4–6 hours

Skills Gained:
- One-to-many JPA mappings
- Cascade and orphan removal patterns
- Nested resource APIs
- Parent-child data consistency
- Real database relationship handling
