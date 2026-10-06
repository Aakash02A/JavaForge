# Project Idea

Build a **Movie Review API** that allows users to post reviews, sort them, and paginate results for a catalog of reviews.

# Problem Statement

Movie review systems often grow large over time, and users need tools to browse reviews by popularity, date, or rating without overwhelming the API response. This project introduces paginated and sorted listings, which is a common requirement in real API design.

# Objectives

- Create a review resource with movie title, reviewer name, rating, and comments.
- Add CRUD operations for reviews.
- Support sorting and pagination for listing reviews.
- Validate all review input fields.
- Learn how to structure result metadata in API responses.

# Features

- Add a review for a movie.
- View all reviews with pagination.
- Sort reviews by different fields.
- Fetch one review by ID.
- Update an existing review.
- Delete a review.
- Handle invalid page and sort values gracefully.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Validation API
- Maven
- In-memory data storage

# Architecture and Flow

The structure stays simple but realistic:

- Controller receives HTTP requests.
- Service validates review payloads and applies sorting/pagination logic.
- Repository stores all reviews in a thread-safe in-memory collection.
- DTOs keep request and response shapes predictable.

The key learning focus here is pagination and sorting. Pagination keeps responses manageable while sorting improves the user experience when many reviews exist.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The app listens on:

```text
http://localhost:8080
```

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/reviews` | Create a review | `201 Created` |
| `GET` | `/api/reviews` | List reviews with pagination and sorting | `200 OK` |
| `GET` | `/api/reviews/{id}` | Get one review | `200 OK` |
| `PUT` | `/api/reviews/{id}` | Replace a review | `200 OK` |
| `DELETE` | `/api/reviews/{id}` | Delete a review | `204 No Content` |

The API validates:

- Movie title must be present and under 200 characters.
- Reviewer name must be present and under 100 characters.
- Rating must be between 1 and 5.
- Comment must not be blank and must remain under 2,000 characters.

Pagination parameters:

- `page`: zero-based page index, default `0`
- `size`: page size, default `10`
- `sortBy`: `createdAt`, `movieTitle`, `reviewerName`, or `rating`
- `direction`: `asc` or `desc`

Invalid parameter combinations return `400 Bad Request`, while unknown IDs return `404 Not Found`.

# Example Requests

```bash
curl -i -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -d '{"movieTitle":"Arrival","reviewerName":"Ari","rating":5,"comment":"Excellent science fiction"}'

curl "http://localhost:8080/api/reviews?page=0&size=5&sortBy=rating&direction=desc"

curl -i http://localhost:8080/api/reviews/999
curl -i -X DELETE http://localhost:8080/api/reviews/1
```

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 3–5 hours

Skills Gained:
- Pagination design
- Sorting criteria in REST APIs
- Validation and API contracts
- API metadata responses
- Real-world backend list behavior
