# 30 - Blog API

Build a **Blog API** that models authors, blog posts, and comments with real entity relationships in a JPA-based backend.

# Problem Statement

A blog application is not just a list of posts; it has relationships between entities. An author can write many posts, and each post can receive many comments. Without careful design, relationships become recursive, difficult to serialize, and harder to validate. This project teaches how to map and manage these relationships in a Spring Boot API.

# Objectives

- Model a real blog domain with JPA relationships.
- Create and retrieve authors, posts, and comments.
- Use DTOs instead of exposing JPA entities directly.
- Manage parent-child ownership correctly.
- Learn cascade deletion and nested retrieval patterns.

# Features

- Create authors with unique email addresses.
- Create blog posts for an author.
- Add comments to posts.
- Get a post with its author and comments.
- List posts with nested summaries.
- Delete a post and its comment records.
- Validate all relationship payloads.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate ORM
- H2 in-memory database
- Maven

# Architecture and Flow

This project demonstrates a relationship-heavy domain:

- `Author` has many `Post` objects.
- `Post` belongs to one `Author` and has many `Comment` objects.
- The API uses DTOs to keep JSON responses clean and avoid recursion.
- JPA handles entity persistence and relationship mapping.

The main design decision is to avoid returning JPA entities directly to clients, because nested entity graphs can easily lead to infinite recursion or unstable JSON responses.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The app runs at:

```text
http://localhost:8080
```

The H2 console is available at:

```text
http://localhost:8080/h2-console
```

Use the JDBC URL:

```text
jdbc:h2:mem:blog
```

with username `sa` and an empty password.

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/authors` | Create an author | `201 Created` |
| `GET` | `/api/authors` | List authors | `200 OK` |
| `POST` | `/api/posts` | Create a post for an author | `201 Created` |
| `GET` | `/api/posts` | List posts | `200 OK` |
| `GET` | `/api/posts/{id}` | Get one post and nested data | `200 OK` |
| `PUT` | `/api/posts/{id}` | Update a post | `200 OK` |
| `POST` | `/api/posts/{id}/comments` | Add a comment | `201 Created` |
| `GET` | `/api/posts/{id}/comments/{commentId}` | Get a specific comment | `200 OK` |
| `DELETE` | `/api/posts/{id}` | Delete a post and its comments | `204 No Content` |

Validation rules:

- Author email must be valid and unique.
- Post title and content are required.
- Commenter name and content are required.
- Missing authors or posts return `404 Not Found`.
- Invalid payloads return `400 Bad Request`.
- Duplicate author emails return `409 Conflict`.

# Example Requests

```bash
curl -i -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{"name":"Writer","email":"writer@example.com","bio":"Software writer"}'

curl -i -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"JPA Relationships","content":"Authors have many posts.","authorId":1}'

curl -i -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -d '{"commenterName":"Reader","content":"Great explanation!"}'

curl http://localhost:8080/api/posts/1
curl -i -X DELETE http://localhost:8080/api/posts/1
```

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 4–6 hours

Skills Gained:
- One-to-many and many-to-one mapping
- JPA relationship management
- DTO design and serialization control
- Cascade behavior and cleanup
- Real-world data modeling for blogs

## Tests

Run `mvn test` to execute `BlogApiIntegrationTest`. This Spring Boot integration test exercises the API and its post/comment persistence relationships using the configured test database.
