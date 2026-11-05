# 33 - School Management

Build a **School Management API** that represents the academic relationship between students, courses, and enrollments using Hibernate and JPA.

# Problem Statement

A simple many-to-many association between students and courses is not enough when you need to store grade and enrollment date information. This project introduces the classic `Enrollment` entity pattern, where relationship data belongs to the join record rather than the student or the course alone. This is how many real school systems model academic progress.

# Objectives

- Model students, courses, and enrollment records.
- Use an `Enrollment` entity to store grade and enrollment date.
- Support multiple student-course relationship operations.
- Apply optimistic locking and validation.
- Keep API payloads clean by exposing DTOs instead of raw entities.

# Features

- Create students and courses.
- Enroll a student in a course.
- Update enrollment grades.
- List student and course enrollments.
- Remove enrollment relationships.
- Delete student or course records while cleaning up related enrollments.
- Protect against duplicate emails and duplicate course codes.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate ORM
- H2 in-memory database
- Maven

# Architecture and Flow

This project is an advanced JPA example with a relationship entity:

- `Student` can have many enrollments.
- `Course` can have many enrollments.
- `Enrollment` belongs to both ends and stores academic metadata such as grade and enrollment date.
- Hibernate handles entity mapping, optimistic locking, and relationship persistence.
- DTOs keep the API safe and avoid serializing persistence entities directly.

This is an important step toward realistic domain modeling because the join table itself holds data that matters to the business.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The API is available at:

```text
http://localhost:8080
```

The H2 console is also available:

```text
http://localhost:8080/h2-console
```

with JDBC URL:

```text
jdbc:h2:mem:school
```

and username `sa`.

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/students` | Create a student | `201 Created` |
| `GET` | `/api/students` | List students | `200 OK` |
| `GET` | `/api/students/{id}` | Get one student | `200 OK` |
| `PUT` | `/api/students/{id}` | Update a student | `200 OK` |
| `DELETE` | `/api/students/{id}` | Delete a student and enrollments | `204 No Content` |
| `GET` | `/api/students/{studentId}/enrollments` | List a student's enrollments | `200 OK` |
| `POST` | `/api/students/{studentId}/courses/{courseId}` | Enroll a student in a course | `201 Created` |
| `PUT` | `/api/students/{studentId}/enrollments/{enrollmentId}` | Update an enrollment grade | `200 OK` |
| `DELETE` | `/api/students/{studentId}/enrollments/{enrollmentId}` | Unenroll a student | `204 No Content` |
| `POST` | `/api/courses` | Create a course | `201 Created` |
| `GET` | `/api/courses` | List courses | `200 OK` |
| `GET` | `/api/courses/{id}` | Get one course | `200 OK` |
| `PUT` | `/api/courses/{id}` | Update a course | `200 OK` |
| `DELETE` | `/api/courses/{id}` | Delete a course and enrollments | `204 No Content` |
| `GET` | `/api/courses/{courseId}/enrollments` | List a course's enrollments | `200 OK` |

Validation rules:

- Student email must be valid and unique.
- Course code must be unique and normalized to uppercase.
- Credits must be between 1 and 6.
- Student date of birth must be in the past.
- Grade must be between 0 and 100 when provided.

Business rules:

- Duplicate emails, course codes, or enrollment pairs return `409 Conflict`.
- Missing records return `404 Not Found`.
- Invalid requests return `400 Bad Request`.
- Hibernate optimistic locking helps prevent lost updates under concurrency.

# Example Requests

```bash
curl -i -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"Mina Patel","email":"mina@school.example","dateOfBirth":"2005-06-15"}'

curl -i -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{"code":"CS101","title":"Computer Science","credits":4}'

curl -i -X POST http://localhost:8080/api/students/1/courses/1

curl -i -X PUT http://localhost:8080/api/students/1/enrollments/1 \
  -H "Content-Type: application/json" \
  -d '{"grade":95}'

curl http://localhost:8080/api/courses/1/enrollments
```

# Project Details

Difficulty: ⭐⭐⭐⭐☆ (Advanced)

Estimated Time: 5–8 hours

Skills Gained:
- Association entity modeling
- Hibernate relationship management
- Optimistic locking
- Grade and enrollment lifecycle design
- Real-world academic domain modeling

## Tests

Run `mvn test` to execute `SchoolManagementIntegrationTest`. It exercises course enrollment and grade workflows with the configured test database, including persistence and association behavior.
