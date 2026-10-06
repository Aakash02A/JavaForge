# Project Idea

Build a **Student API** that manages student records with validation, unique email handling, and clean CRUD behavior.

# Problem Statement

When student records are managed manually, it is easy to make mistakes such as duplicate entries, invalid email values, or inconsistent course information. This project solves that problem by enforcing validation rules and giving the API a clear, predictable structure.

# Objectives

- Create, read, update, and delete student records.
- Validate request fields before saving data.
- Prevent duplicate emails.
- Normalize and sanitize common input values.
- Practice layered backend development with Spring Boot.

# Features

- Add new students with full name, age, email, and course.
- List all students in creation order.
- Get one student by ID.
- Update a student's details.
- Delete a student record.
- Reject invalid email, age, and blank values.
- Prevent duplicate email registration.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Bean Validation
- Maven
- In-memory storage

# Architecture and Flow

This project introduces a cleaner service-based architecture while still keeping data in memory:

- Controller receives web requests.
- Service validates and applies business logic.
- Repository keeps student records in a thread-safe collection.
- DTO records define the API contract.

The application normalizes emails to lowercase and trims whitespace for consistency. This avoids small input variations creating separate student records.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The app starts at:

```text
http://localhost:8080
```

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/students` | Create a student | `201 Created` |
| `GET` | `/api/students` | List all students | `200 OK` |
| `GET` | `/api/students/{id}` | Get a student by ID | `200 OK` |
| `PUT` | `/api/students/{id}` | Update a student | `200 OK` |
| `DELETE` | `/api/students/{id}` | Delete a student | `204 No Content` |

Validation rules:

- `fullName` is required and must be under 100 characters.
- `email` must be valid and under 254 characters.
- `age` must be between 16 and 120.
- `course` is required and must not be blank.

Business rules:

- Emails are normalized to lowercase.
- Duplicate emails return `409 Conflict`.
- Missing students return `404 Not Found`.
- Invalid payloads return `400 Bad Request`.

# Example Requests

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

# Project Details

Difficulty: ⭐☆☆☆☆ (Beginner)

Estimated Time: 2–4 hours

Skills Gained:
- Spring Boot CRUD APIs
- Validation and conflict handling
- DTO-level input processing
- Error handling in REST services
- Data normalization
