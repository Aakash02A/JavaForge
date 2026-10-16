# 44 - Product Service

A Spring Boot REST microservice that manages a product catalog. It exposes product CRUD operations, enforces unique SKUs, validates incoming data, and persists records through Spring Data JPA.

## Features

- Create, list, retrieve, update, and delete products.
- Enforce unique product SKUs.
- Validate required fields, prices, and stock quantities.
- Persist product data with JPA and Hibernate.
- Return creation and update timestamps.
- Use an embedded H2 database by default for local development.
- Return consistent JSON errors for invalid, duplicate, and missing products.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA and Hibernate
- H2 Database
- Spring Cloud Netflix Eureka Client
- Spring Cloud Config Client
- Maven
- JUnit 5 and MockMvc

## Run locally

Start module 49 (Config Server) and module 48 (Service Discovery) in separate terminals:

```powershell
cd 49-Config-Server
mvn spring-boot:run
```

```powershell
cd 48-Service-Discovery
mvn spring-boot:run
```

Then start the service:

```powershell
cd 44-Product-Service
mvn clean test
mvn spring-boot:run
```

The service listens on `http://localhost:8080`, loads central defaults from Config Server at `http://localhost:8888`, and registers with Eureka at `http://localhost:8761/eureka/` by default. Configure `CONFIG_SERVER_URL` and `EUREKA_SERVER_URL` to use different servers, or set `SPRING_CLOUD_CONFIG_ENABLED=false` and `EUREKA_CLIENT_ENABLED=false` to disable the clients. The default H2 database is in-memory and resets when the application stops.

## API

All product endpoints are under `/api/products`.

### Create a product

`POST /api/products`

```json
{
  "sku": "KB-001",
  "name": "Mechanical Keyboard",
  "description": "Compact wireless keyboard",
  "price": 89.99,
  "stockQuantity": 25
}
```

Returns `201 Created` and the new product, including its ID and timestamps.

### List products

`GET /api/products`

Returns all products as a JSON array.

### Get a product

`GET /api/products/{id}`

Returns the product or `404 Not Found`.

### Update a product

`PUT /api/products/{id}`

Accepts the same fields as product creation and replaces the product's editable fields.

### Delete a product

`DELETE /api/products/{id}`

Returns `204 No Content` when the product is deleted. Returns `404 Not Found` if the ID does not exist.

## Validation and errors

- SKU is required and limited to 64 characters.
- Name is required and limited to 120 characters.
- Description is required and limited to 1,000 characters.
- Price must be greater than zero, with at most 10 integer digits and 2 fractional digits.
- Stock quantity must be zero or greater.
- SKU values are unique.

| Status | Meaning |
| --- | --- |
| `200 OK` | Product retrieved or updated |
| `201 Created` | Product created |
| `204 No Content` | Product deleted |
| `400 Bad Request` | Request JSON or validation failed |
| `404 Not Found` | Product ID does not exist |
| `409 Conflict` | SKU is already assigned to another product |

Errors return a JSON object with timestamp, HTTP status, error label, and message.

## Database configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `DATABASE_URL` | `jdbc:h2:mem:productdb` | JDBC connection URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | empty | Database password |
| `H2_CONSOLE_ENABLED` | `false` | Enable the local H2 console |

The H2 console can be enabled for local debugging with `$env:H2_CONSOLE_ENABLED = "true"`; it is disabled by default. To connect to another JDBC database, provide its JDBC URL and the appropriate driver dependency.

## Architecture

```text
HTTP client
    |
    v
ProductController -- REST endpoints and request validation
    |
    v
ProductService -- business rules and SKU uniqueness
    |
    v
ProductRepository -- Spring Data JPA
    |
    v
H2 database (local default)
```

## Tests

```powershell
mvn test
```

Integration tests use MockMvc and the in-memory H2 database. They cover CRUD operations, duplicate SKU conflicts, request validation, and missing products.

## Microservices note

This service owns the product catalog and can be consumed by later order or gateway services over HTTP. The default embedded database and lack of authentication are for learning and local development; production deployments should use an external database, service authentication, migrations, and operational health/metrics.
