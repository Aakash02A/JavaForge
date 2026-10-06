# Project Idea

Build a **Notes API** that stores personal notes, reminders, and quick thoughts in a simple REST application.

# Problem Statement

When people capture ideas on paper or in scattered tools, they often lose structure and consistency. A simple note-taking API solves that by providing a clean interface to create, update, read, and delete notes. It also introduces a good pattern for managing business validation in a Spring Boot service.

# Objectives

- Implement a CRUD API for notes.
- Validate title and body fields before accepting input.
- Practice request/response DTO design.
- Build a service layer for business operations.
- Learn how to return consistent errors for invalid and missing records.

# Features

- Create a note with a title and content.
- Retrieve all notes.
- Fetch one note by ID.
- Replace a note's content.
- Delete a note when no longer needed.
- Reject blank or oversized note values.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Bean Validation
- Maven
- In-memory data storage

# Architecture and Flow

The project keeps the logic organized in small layers:

- Controller: receives HTTP requests from the client.
- Service: applies business rules and decides if requests are valid.
- Repository: stores note objects in a thread-safe collection.
- DTOs: carry request payloads and help validate input.

This makes the project a strong example of how larger applications evolve from a simple model into a layered architecture without needing a database yet.

# Setup and Run

From the project directory, start the application:

```bash
mvn spring-boot:run
```

The service runs at:

```text
http://localhost:8080
```

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/notes` | Create a note | `201 Created` |
| `GET` | `/api/notes` | List all notes | `200 OK` |
| `GET` | `/api/notes/{id}` | Get a single note | `200 OK` |
| `PUT` | `/api/notes/{id}` | Replace a note | `200 OK` |
| `DELETE` | `/api/notes/{id}` | Delete a note | `204 No Content` |

Validation is strict:

- Title is required and cannot be blank.
- Title length is limited to 120 characters.
- Content is required and cannot be blank.
- Content length is capped at 10,000 characters.
- Unknown note IDs return `404 Not Found`.
- Invalid request bodies return `400 Bad Request`.

# Example Requests

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

# Project Details

Difficulty: ⭐☆☆☆☆ (Beginner)

Estimated Time: 2–4 hours

Skills Gained:
- REST CRUD design
- Input validation
- Layered service architecture
- Error-handling patterns
- Spring Boot API basics
