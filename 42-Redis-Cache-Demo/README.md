# 42 - Redis Cache Demo

A Spring Boot product API that demonstrates the cache-aside pattern using Redis. Product data is read from a small in-memory catalog on cache misses, cached with a configurable TTL, and evicted after updates. The Redis cache itself is real and required when running the application.

## Features

- Cache product reads in Redis using string keys and JSON values.
- Report whether each product response came from a cache hit or miss.
- Expire cached products after a configurable time-to-live.
- Invalidate cached entries after product updates or explicit eviction requests.
- Expose process-local hit, miss, and eviction counters.
- Return consistent errors for missing products, invalid input, and Redis outages.
- Run Redis locally with Docker Compose.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Web and Validation
- Spring Data Redis
- Redis 7
- Maven
- JUnit 5, Mockito, and MockMvc

## Run locally

Start Redis:

```powershell
cd 42-Redis-Cache-Demo
docker compose up -d
```

Run the tests and start the API:

```powershell
mvn clean test
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. To stop Redis, run `docker compose down`. Its local append-only data is stored in the ignored `redis-data/` directory.

## API

### Read a product

`GET /api/products/{id}`

The demo catalog starts with product IDs `1`, `2`, and `3`. The first request for an uncached product loads it from the catalog and saves it in Redis; subsequent requests return the cached value until expiry or invalidation.

```powershell
Invoke-RestMethod http://localhost:8080/api/products/1
```

Response:

```json
{
  "product": {
    "id": 1,
    "name": "Mechanical Keyboard",
    "description": "Compact wireless keyboard",
    "price": 89.99
  },
  "cacheStatus": "MISS"
}
```

The next read returns `"cacheStatus": "HIT"`.

### Update a product

`PUT /api/products/{id}`

```powershell
$body = @{
  name = "Updated Keyboard"
  description = "Updated product description"
  price = 99.00
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Put `
  -Uri http://localhost:8080/api/products/1 `
  -ContentType "application/json" `
  -Body $body
```

Updates to known catalog IDs immediately evict the corresponding Redis entry. Product data is intentionally in memory, so catalog edits do not survive an application restart.

### Evict a cached product

`DELETE /api/cache/products/{id}`

Returns the product ID and whether a cache entry was removed. Evicting a missing entry is safe and returns `"evicted": false`.

### View cache counters

`GET /api/cache/stats`

Returns hits, misses, and successful evictions since this application instance started. These counters are local to the process and are not Redis-wide metrics.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `REDIS_HOST` | `localhost` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |
| `REDIS_TIMEOUT` | `2s` | Redis command timeout |
| `PRODUCT_CACHE_TTL` | `5m` | How long cached products remain available |

Example PowerShell configuration:

```powershell
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
$env:PRODUCT_CACHE_TTL = "10m"
mvn spring-boot:run
```

## Errors

| Status | Meaning |
| --- | --- |
| `200 OK` | Product read, update, eviction, or stats request succeeded |
| `400 Bad Request` | Product update fields are invalid |
| `404 Not Found` | Product ID does not exist in the catalog |
| `503 Service Unavailable` | Redis is unavailable |

Error responses include a timestamp, HTTP status, error label, and message. Redis failures are surfaced; the service does not silently bypass the configured cache.

## Architecture

```text
HTTP client
    |
    v
ProductController
    |
    v
ProductService -- hit/miss tracking and cache-aside coordination
    |                                  |
    v                                  v
ProductCache                    ProductCatalog
    |                                  |
    v                                  v
RedisProductCache               InMemoryProductCatalog
    |
    v
Redis (JSON values, products:{id} keys, TTL)
```

## Tests

```powershell
mvn test
```

Integration tests mock the Redis-facing cache and in-memory catalog, so tests do not require Docker or a running Redis server. They cover cache hits and misses, cache population, update invalidation, manual eviction, validation, and missing products.

## Limitations

- The product catalog and counters are in-memory demo implementations, not durable storage or distributed metrics.
- Cache-aside reads can briefly race with concurrent updates; production systems may need stronger consistency guarantees.
- This demo surfaces Redis outages instead of falling back to uncached catalog reads.
- Configure Redis authentication, TLS, network restrictions, and production secrets in the deployment environment before exposing the service.
