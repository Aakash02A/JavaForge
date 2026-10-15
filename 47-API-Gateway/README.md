# 47 - API Gateway

A reactive Spring Cloud Gateway that provides one HTTP entry point for the Product, Order, and Payment services. It routes requests without changing their `/api/...` paths or payloads, and returns consistent JSON responses when an upstream service cannot be reached.

## Features

- Route product API requests to module 44.
- Route order API requests to module 45.
- Route payment API requests to module 46.
- Preserve request method, path, query string, headers, and body.
- Configure downstream URLs and gateway timeouts using environment variables.
- Map upstream connection failures to `502 Bad Gateway` and upstream timeouts to `504 Gateway Timeout`.
- Use Spring WebFlux and Reactor Netty; no servlet MVC server is used.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Cloud Gateway (Spring Cloud 2025.0.0)
- Spring WebFlux and Reactor Netty
- Maven, JUnit 5, and WebTestClient

## Run the services

Start each service in a separate terminal:

```powershell
cd 44-Product-Service
mvn spring-boot:run
```

```powershell
cd 45-Order-Service
mvn spring-boot:run
```

```powershell
cd 46-Payment-Service
mvn spring-boot:run
```

Then start the gateway:

```powershell
cd 47-API-Gateway
mvn clean test
mvn spring-boot:run
```

The gateway listens on `http://localhost:8083` by default. Backend modules retain their default ports: Product `8080`, Order `8081`, Payment `8082`.

## Routes

| Gateway path | Destination | Example |
| --- | --- | --- |
| `/api/products` and `/api/products/**` | Product Service | `GET http://localhost:8083/api/products/1` |
| `/api/orders` and `/api/orders/**` | Order Service | `POST http://localhost:8083/api/orders` |
| `/api/payments` and `/api/payments/**` | Payment Service | `GET http://localhost:8083/api/payments/order/42` |

Paths and query strings are forwarded unchanged. Use the downstream service documentation for request bodies and response formats.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8083` | Gateway HTTP port |
| `PRODUCT_SERVICE_URL` | `http://localhost:8080` | Product Service base URL |
| `ORDER_SERVICE_URL` | `http://localhost:8081` | Order Service base URL |
| `PAYMENT_SERVICE_URL` | `http://localhost:8082` | Payment Service base URL |
| `GATEWAY_CONNECT_TIMEOUT_MS` | `2000` | Upstream connection timeout in milliseconds |
| `GATEWAY_RESPONSE_TIMEOUT` | `5s` | Upstream response timeout; Spring duration syntax is supported |

PowerShell example for a different backend address:

```powershell
$env:PRODUCT_SERVICE_URL = "http://localhost:9080"
$env:ORDER_SERVICE_URL = "http://localhost:9081"
$env:PAYMENT_SERVICE_URL = "http://localhost:9082"
mvn spring-boot:run
```

## Errors

For a downstream connection failure, the gateway returns `502 Bad Gateway`; for an upstream timeout, it returns `504 Gateway Timeout`. Both include a JSON body with timestamp, status, error, and a safe message. Other unexpected errors are propagated rather than hidden as upstream errors. An unmatched path returns `404 Not Found`.

The gateway does not currently provide authentication, authorization, rate limiting, service discovery, circuit breaking, or retries. Do not treat it as a security boundary until those policies and trusted network configuration are added.

## Tests

```powershell
mvn test
```

Routing integration tests start an ephemeral local HTTP backend and exercise the running gateway for each service route, including method/body/query forwarding and unmatched paths. Filter tests verify `502` and `504` error mapping. Tests do not require the microservices or Docker.
