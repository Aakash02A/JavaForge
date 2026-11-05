# 38 - URL Shortener

## Project Idea

Build a production-style URL-shortening API that accepts a long web address, generates a compact code, persists the mapping, and redirects clients back to the original address.

## Problem Statement

Long URLs are difficult to share in messages, QR codes, printed material, and systems with length limits. A shortener needs more than string truncation: it must generate stable codes, prevent collisions, preserve the original URL, validate unsafe input, and resolve redirects efficiently.

## Objectives

- Expose a REST endpoint for creating short URLs.
- Generate deterministic codes using SHA-256 and Base62.
- Reuse a mapping when the same URL is submitted more than once.
- Handle code collisions without overwriting an existing mapping.
- Persist mappings with database uniqueness constraints.
- Provide metadata lookup and a public redirect route.
- Track the number of successful redirects.

## Features

- `POST /api/urls` creates or reuses a short URL.
- `GET /api/urls/{code}` returns mapping metadata.
- `GET /{code}` redirects to the original URL.
- Only absolute `http` and `https` URLs are accepted.
- URL normalization trims surrounding whitespace and uses ASCII URI encoding.
- Duplicate original URLs receive the same code.
- Redirects increment a click counter.
- Unknown codes return `404 Not Found`.
- Invalid input returns `400 Bad Request`.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web and embedded Tomcat
- Spring Data JPA and Hibernate
- H2 in-memory database
- Jakarta Bean Validation
- Maven
- JUnit 5 and MockMvc

## Architecture

```text
UrlShortenerController       RedirectController
          |                         |
          +------ UrlShortenerService
                              |
                    ShortUrlRepository
                              |
                         ShortUrl entity
```

The API controller handles JSON requests, while the redirect controller handles public short-code requests. The service owns validation, hashing, collision resolution, duplicate reuse, and click updates. The repository provides database-backed lookups.

## URL Creation Flow

1. The request body supplies a URL.
2. The service trims whitespace and parses it as a URI.
3. Only absolute HTTP and HTTPS URLs with a host are accepted.
4. The normalized ASCII URL is checked for an existing mapping.
5. If present, the existing response is returned without creating another row.
6. Otherwise, SHA-256 is calculated.
7. Digest bytes are encoded with Base62 into an eight-character code.
8. If the code is already occupied, a Base62 attempt suffix is added.
9. A database uniqueness constraint provides a final concurrency safeguard.

## Hashing Details

Hashing makes the code deterministic: the same normalized URL produces the same initial candidate. Base62 uses digits, lowercase letters, and uppercase letters, allowing more values in fewer characters than hexadecimal. The application does not expose the full digest; it uses a compact derived code.

Hashing alone does not make collisions impossible. The service checks the code before insertion and retries with a deterministic suffix. The database also enforces uniqueness for both `original_url` and `short_code`, preventing accidental overwrites.

## Data Model

The `short_urls` table contains:

- `original_url`: validated destination URL.
- `short_code`: unique compact identifier.
- `created_at`: creation timestamp.
- `click_count`: successful redirect count.

The original URL and short code both have unique constraints.

## API Endpoints

| Method | Path | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/api/urls` | Public | Create or reuse a mapping |
| `GET` | `/api/urls/{code}` | Public | Return mapping metadata |
| `GET` | `/{code}` | Public | Redirect to the destination and count a click |

### Create request

```json
{
  "url": "https://example.com/products?id=42"
}
```

### Create response

```json
{
  "shortUrl": "http://localhost:8080/2cY8rG2Q",
  "originalUrl": "https://example.com/products?id=42",
  "createdAt": "2026-10-12T06:30:00Z",
  "clickCount": 0
}
```

## Setup and Run

```bash
cd 38-URL-Shortener
mvn clean test
mvn package
java -jar target/38-url-shortener-1.0-SNAPSHOT.jar
```

Set the public prefix when running behind a domain or reverse proxy:

```powershell
$env:PUBLIC_BASE_URL = "https://short.example.com"
```

No external database is needed for local use. H2 data is cleared when the process stops.

## Testing

The integration tests verify:

- Deterministic duplicate reuse.
- Exactly one database record for repeated input.
- Mapping lookup.
- Redirect target and click count.
- Invalid URL rejection.
- Unknown-code `404` behavior.

Run:

```bash
mvn test
mvn package
```

## Security and Production Considerations

This milestone validates destinations but does not provide malware scanning, phishing detection, authentication, abuse prevention, rate limiting, or domain allowlists. Production deployments should add those controls, use a durable database, protect administrative analytics, and consider asynchronous click aggregation for high traffic.

## Learning Outcomes

- Design a Spring Boot REST service around a focused domain model.
- Use cryptographic hashing as a deterministic identifier source.
- Convert numeric values to a compact Base62 representation.
- Combine application-level collision checks with database constraints.
- Separate lookup metadata from redirect behavior.
- Test persistence and HTTP behavior with MockMvc.
