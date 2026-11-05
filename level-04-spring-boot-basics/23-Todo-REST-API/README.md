# 23 - Todo REST API

Build a **Todo REST API** that helps users create, track, update, and remove day-to-day tasks using a clean REST interface.

# Problem Statement

Most real-world task management tools start as a simple list of actions that need to be completed. A developer needs a reliable way to create tasks, mark them complete, and query them later without storing data in a database at this stage. This project focuses on the essential backend skills of HTTP handling, validation, and layered application design.

# Objectives

- Create a Spring Boot API for task management.
- Implement CRUD operations for todo records.
- Validate incoming request payloads.
- Return consistent errors for bad input and missing resources.
- Learn REST design patterns before adding database persistence.

# Features

- Create a todo item with a title and optional description.
- View all todos in creation order.
- Fetch a single todo by ID.
- Update todo details or change completion state.
- Delete a todo record.
- Validate blank titles and oversized text.
- Return structured 400 and 404 responses.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Validation API
- Maven
- In-memory storage

# Architecture and Flow

This project follows a layered backend structure that keeps the API clean and easy to extend.

- Controller layer handles incoming HTTP requests.
- Service layer contains the business rules.
- Repository or storage layer keeps in-memory task data.
- DTOs are used to validate and transfer request data.

The application stores todos in a thread-safe in-memory collection. Because the data is not persistent, it is reset whenever the app stops. This is intentional and helps beginners focus on REST fundamentals instead of database configuration.

# Setup and Run

From this project directory, start the application:

```bash
mvn spring-boot:run
```

The API listens on:

```text
http://localhost:8080
```

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/todos` | Create a todo | `201 Created` |
| `GET` | `/api/todos` | List all todos | `200 OK` |
| `GET` | `/api/todos/{id}` | Get a single todo | `200 OK` |
| `PUT` | `/api/todos/{id}` | Replace todo details | `200 OK` |
| `PATCH` | `/api/todos/{id}/completion` | Toggle completion status | `200 OK` |
| `DELETE` | `/api/todos/{id}` | Delete a todo | `204 No Content` |

Validation rules include:

- Title is required and must not be blank.
- Title length is capped at 120 characters.
- Description is optional but limited to 2,000 characters.
- Unknown IDs return `404 Not Found`.
- Invalid payloads return `400 Bad Request` with detailed field errors.

# Example Requests

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

# Project Details

Difficulty: ⭐☆☆☆☆ (Beginner)

Estimated Time: 2–4 hours

Skills Gained:
- REST API design
- Java Spring Boot basics
- DTO validation
- Exception handling
- CRUD operations
- Real HTTP testing with curl

## Tests

Run `mvn test` to execute `TodoControllerTest` and `TodoServiceTest`. The controller tests exercise HTTP status codes, response bodies, validation, and not-found responses; the service tests cover in-memory todo lifecycle behavior. The tests do not require an external database.
