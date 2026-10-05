# Library API

A Java 21 / Spring Boot 3 REST API demonstrating a one-to-many Spring Data JPA
relationship. A library has many books; each book belongs to exactly one
library. The parent `Library` manages its child books with cascade persistence
and orphan removal. DTOs expose nested data without recursive entity JSON.

The project uses an H2 in-memory database by default, so no database server is
needed. Data resets when the application stops.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`. The H2 console is available at
`http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:library`,
username `sa`, and an empty password.

## Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/libraries` | Create a library | `201 Created` |
| `GET` | `/api/libraries` | List libraries and their books | `200 OK` |
| `GET` | `/api/libraries/{libraryId}` | Get a library and its books | `200 OK` |
| `PUT` | `/api/libraries/{libraryId}` | Update library details | `200 OK` |
| `POST` | `/api/libraries/{libraryId}/books` | Add a book to a library | `201 Created` |
| `GET` | `/api/libraries/{libraryId}/books/{bookId}` | Get a book belonging to a library | `200 OK` |
| `PUT` | `/api/libraries/{libraryId}/books/{bookId}` | Update a book | `200 OK` |
| `DELETE` | `/api/libraries/{libraryId}/books/{bookId}` | Remove a book from its library | `204 No Content` |
| `DELETE` | `/api/libraries/{libraryId}` | Delete a library and its books | `204 No Content` |

Library names, addresses, book titles, authors, and ISBN values are required.
Publication years must be between 1450 and 2100. Invalid input returns `400`;
unknown libraries or books return `404`.

## Example

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

## Test

```bash
mvn test
mvn package
```

H2 integration tests cover parent/child persistence, nested retrieval, book
ownership, orphan removal, cascade deletion, validation, and not-found behavior.
