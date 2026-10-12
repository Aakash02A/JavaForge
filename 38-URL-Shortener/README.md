# 38 - URL Shortener

A persistent Spring Boot URL-shortening API that converts long HTTP/HTTPS URLs into deterministic, compact Base62 codes.

## Features

- Create a short URL from an absolute HTTP or HTTPS URL.
- Return the same short code when the same URL is submitted again.
- Generate codes from SHA-256 hashing and Base62 encoding.
- Detect short-code collisions and select a deterministic suffix.
- Resolve a code through a JSON lookup endpoint.
- Redirect from the public short URL.
- Count successful redirects.
- Store mappings in an H2/JPA database.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA and Hibernate
- H2
- Jakarta Bean Validation
- Maven
- JUnit 5 and MockMvc

## Run

```bash
cd 38-URL-Shortener
mvn clean test
mvn package
java -jar target/38-url-shortener-1.0-SNAPSHOT.jar
```

The service runs at `http://localhost:8080`. It uses an in-memory H2 database and requires no external database server.

## API

### Create a short URL

```bash
curl -i -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/articles/java/url-shortening"}'
```

Example response:

```json
{
  "shortUrl": "http://localhost:8080/2cY8rG2Q",
  "originalUrl": "https://example.com/articles/java/url-shortening",
  "createdAt": "2026-10-12T06:30:00Z",
  "clickCount": 0
}
```

### Look up a mapping

```bash
curl -i http://localhost:8080/api/urls/2cY8rG2Q
```

### Follow a short URL

```bash
curl -i http://localhost:8080/2cY8rG2Q
```

The response is an HTTP redirect to the original URL and increments `clickCount`.

## Hashing and Collision Handling

The service normalizes and validates the input URL, computes a SHA-256 digest, converts the first digest bytes to an unsigned Base62 value, and pads the result to an eight-character code. If an eight-character collision is found, the service appends a Base62 attempt suffix and checks the database before saving.

The original URL and short code have database uniqueness constraints. This protects the mapping even if two requests arrive concurrently.

## Configuration

The public URL prefix can be changed for deployment:

```powershell
$env:PUBLIC_BASE_URL = "https://short.example.com"
```

The default value is `http://localhost:8080`.

## Tests

```bash
mvn test
```

The integration suite checks deterministic duplicate reuse, JSON lookup, redirect behavior, click counting, invalid URLs, and unknown codes.
