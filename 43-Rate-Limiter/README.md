# 43 - Rate Limiter

A Spring Boot API demonstrating a Redis-backed, per-client fixed-window rate limiter. A single atomic Redis Lua script increments each client's request count and applies the window expiry, so concurrent application instances share the same limit.

## Features

- Limit requests by the caller's remote IP address.
- Share counters across application instances using Redis.
- Perform counter increment and first-request expiry atomically with Lua.
- Return HTTP `429 Too Many Requests` after the configured number of requests.
- Include rate-limit headers and a `Retry-After` value.
- Configure the request limit, window, and Redis connection using environment variables.
- Fail with HTTP `503 Service Unavailable` when Redis cannot enforce the limit; requests are not silently allowed through.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data Redis
- Redis 7
- Maven
- JUnit 5, Mockito, and MockMvc

## Run locally

Start Redis:

```powershell
cd 43-Rate-Limiter
docker compose up -d
```

Run tests and start the API:

```powershell
mvn clean test
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. Stop Redis with `docker compose down`. Redis persistence files are stored in the ignored `redis-data/` directory.

## Try the rate-limited endpoint

`GET /api/limited`

The default policy allows five requests per client IP in each one-minute fixed window. Try calling it repeatedly:

```powershell
1..7 | ForEach-Object {
  try {
    Invoke-WebRequest http://localhost:8080/api/limited
  } catch {
    $_.Exception.Response
  }
}
```

An allowed request responds with HTTP `200 OK`:

```json
{
  "allowed": true,
  "message": "Request allowed",
  "limit": 5,
  "remaining": 4,
  "resetAfterMillis": 60000
}
```

Once the limit is exceeded, the API responds with HTTP `429 Too Many Requests`, `"allowed": false`, and a `Retry-After` header.

## Response headers

| Header | Meaning |
| --- | --- |
| `X-RateLimit-Limit` | Maximum allowed requests per window |
| `X-RateLimit-Remaining` | Requests remaining in the current window |
| `X-RateLimit-Reset-Ms` | Approximate milliseconds until the current window expires |
| `Retry-After` | Seconds to wait before retrying; included on limited responses |

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `REDIS_HOST` | `localhost` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |
| `REDIS_TIMEOUT` | `2s` | Redis command timeout |
| `RATE_LIMIT_REQUESTS` | `5` | Allowed requests for each client in one window |
| `RATE_LIMIT_WINDOW` | `1m` | Fixed-window duration; Spring duration syntax such as `30s` or `5m` |

Example PowerShell configuration:

```powershell
$env:RATE_LIMIT_REQUESTS = "10"
$env:RATE_LIMIT_WINDOW = "30s"
mvn spring-boot:run
```

## How it works

Redis uses a key such as `rate-limit:192.0.2.10` for each remote IP address. An atomic Lua script increments the key, sets its expiry only on the first request, and returns both the count and remaining TTL. The service then decides whether to allow the request.

This is a fixed-window algorithm: the counter resets when its Redis key expires. A client can make requests on either side of a window boundary, so short bursts across that boundary can exceed the configured number over a brief rolling interval.

## Errors and operational notes

| Status | Meaning |
| --- | --- |
| `200 OK` | Request is within the configured limit |
| `429 Too Many Requests` | Client has exhausted its current window |
| `503 Service Unavailable` | Redis could not enforce the policy |

- The demo keys by the servlet container's remote IP. When deployed behind a proxy, configure trusted proxy handling at the server or gateway; do not blindly trust caller-supplied forwarding headers.
- Redis is part of request processing. If Redis is down, the limiter fails closed with `503` rather than allowing unmetered traffic.
- Protect Redis with network restrictions, authentication, and TLS appropriate to your deployment. The local Compose configuration is for development only.
- Consider a sliding-window or token-bucket algorithm when fixed-window boundary bursts are unsuitable.

## Tests

```powershell
mvn test
```

Tests mock the Redis store and do not require Docker or a running Redis service. They cover allowed and rejected decisions, response headers, Redis script interaction, and unavailable-store handling.
