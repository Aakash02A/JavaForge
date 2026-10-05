# Blog API

A Java 21 / Spring Boot 3 REST API demonstrating Spring Data JPA relationships.
Authors have many posts; each post belongs to one author and can have many
comments. DTOs are used for request and response payloads to avoid exposing JPA
entities or recursive relationship serialization.

The project uses an H2 in-memory database by default, requiring no database
server. Data resets when the application stops.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`. The H2 console is available at
`http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:blog`, username
`sa`, and an empty password.

## Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/authors` | Create an author | `201 Created` |
| `GET` | `/api/authors` | List authors with post counts | `200 OK` |
| `POST` | `/api/posts` | Create a post for an author | `201 Created` |
| `GET` | `/api/posts` | List posts with author and comments | `200 OK` |
| `GET` | `/api/posts/{id}` | Get a post with its author and comments | `200 OK` |
| `PUT` | `/api/posts/{id}` | Replace post content or author | `200 OK` |
| `POST` | `/api/posts/{id}/comments` | Add a comment to a post | `201 Created` |
| `GET` | `/api/posts/{id}/comments/{commentId}` | Get a comment on a post | `200 OK` |
| `DELETE` | `/api/posts/{id}` | Delete a post and its comments | `204 No Content` |

Author email must be valid and unique. Post titles and content, author IDs,
commenter names, and comment content are validated. Invalid input returns `400`;
missing authors or posts return `404`; duplicate author emails return `409`.

## Example

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

## Test

```bash
mvn test
mvn package
```

Integration tests use H2 and cover relationship persistence, nested comment
creation, post counts, cascading post deletion, validation, and missing authors.
