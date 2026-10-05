# Movie Review API

A paginated Movie Review API built with Java 21 and Spring Boot 3. It uses
separate controller, service, and repository layers with thread-safe in-memory
storage. Data is cleared when the application stops; persistence is introduced
in later Spring Data JPA projects.

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
| `POST` | `/api/reviews` | Create a review | `201 Created` with `Location` |
| `GET` | `/api/reviews` | List reviews using pagination and sorting | `200 OK` |
| `GET` | `/api/reviews/{id}` | Get one review | `200 OK` |
| `PUT` | `/api/reviews/{id}` | Replace a review | `200 OK` |
| `DELETE` | `/api/reviews/{id}` | Delete a review | `204 No Content` |

Reviews require a movie title (up to 200 characters), reviewer name (up to 100
characters), a rating from 1 to 5, and a non-blank comment (up to 2,000
characters). Invalid request bodies return `400`; missing reviews return `404`.

## Pagination

`GET /api/reviews` accepts:

- `page`: zero-based page number; defaults to `0`
- `size`: page size from 1 to 100; defaults to `10`
- `sortBy`: `createdAt`, `movieTitle`, `reviewerName`, or `rating`; defaults to `createdAt`
- `direction`: `asc` or `desc`; defaults to `desc`

The response includes `content`, `page`, `size`, `totalElements`, `totalPages`,
`first`, and `last`. Invalid pagination or sorting parameters return `400`.

## Example

```bash
curl -i -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -d '{"movieTitle":"Arrival","reviewerName":"Ari","rating":5,"comment":"Excellent science fiction"}'
curl "http://localhost:8080/api/reviews?page=0&size=5&sortBy=rating&direction=desc"
curl -i http://localhost:8080/api/reviews/999
curl -i -X DELETE http://localhost:8080/api/reviews/1
```

## Test

```bash
mvn test
mvn package
```

Tests cover CRUD, pagination metadata and sorting, invalid parameters,
validation, and not-found handling.
