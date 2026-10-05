# Notes API

A layered Notes CRUD API built with Java 21 and Spring Boot 3. Notes are stored
in thread-safe in-memory storage and are cleared when the application stops.
This project focuses on CRUD and HTTP fundamentals; database persistence is
covered by later projects.

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
| `POST` | `/api/notes` | Create a note | `201 Created` with `Location` |
| `GET` | `/api/notes` | List notes in creation order | `200 OK` |
| `GET` | `/api/notes/{id}` | Get one note | `200 OK` |
| `PUT` | `/api/notes/{id}` | Replace a note's title and content | `200 OK` |
| `DELETE` | `/api/notes/{id}` | Delete a note | `204 No Content` |

Titles are required, must contain non-whitespace text, and may be at most 120
characters. Content is required, must contain non-whitespace text, and may be
at most 10,000 characters. Missing notes return `404`; invalid request bodies
return `400` with field-level validation errors.

## Example

```bash
curl -i -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title":"Meeting","content":"Discuss the project roadmap"}'
curl http://localhost:8080/api/notes
curl -X PUT http://localhost:8080/api/notes/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Team meeting","content":"Review the project roadmap"}'
curl -i http://localhost:8080/api/notes/999
curl -i -X DELETE http://localhost:8080/api/notes/1
```

## Test

```bash
mvn test
mvn package
```

Automated tests cover note creation, listing, updates, deletion, request
validation, HTTP status codes, and not-found handling.
