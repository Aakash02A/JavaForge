# 47 - API Gateway

A reactive Spring Cloud Gateway that provides one HTTP entry point for the Product, Order, and Payment services. It discovers registered service instances through Eureka, routes requests without changing their `/api/...` paths or payloads, and returns consistent JSON responses when an upstream service cannot be reached.

## Features

- Route product API requests to module 44.
- Route order API requests to module 45.
- Route payment API requests to module 46.
- Preserve request method, path, query string, headers, and body.
- Discover downstream services through Eureka, with optional direct URL overrides.
- Map upstream connection failures to `502 Bad Gateway` and upstream timeouts to `504 Gateway Timeout`.
- Use Spring WebFlux and Reactor Netty; no servlet MVC server is used.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Cloud Gateway (Spring Cloud 2025.0.0)
- Spring WebFlux and Reactor Netty
- Maven, JUnit 5, and WebTestClient

## Run the services

Start module 48 (Service Discovery), then start each service in a separate terminal:

```powershell
cd 48-Service-Discovery
mvn spring-boot:run
```

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

The gateway listens on `http://localhost:8083` by default. Backend modules retain their default ports: Product `8080`, Order `8081`, Payment `8082`. Order Service also uses Eureka to discover Product Service.

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
| `EUREKA_SERVER_URL` | `http://localhost:8761/eureka/` | Eureka registry URL |
| `EUREKA_CLIENT_ENABLED` | `true` | Enable Eureka registration and discovery |
| `PRODUCT_SERVICE_URL` | `lb://PRODUCT-SERVICE` | Product Service route URI; direct URL also supported |
| `ORDER_SERVICE_URL` | `lb://ORDER-SERVICE` | Order Service route URI; direct URL also supported |
| `PAYMENT_SERVICE_URL` | `lb://PAYMENT-SERVICE` | Payment Service route URI; direct URL also supported |
| `GATEWAY_CONNECT_TIMEOUT_MS` | `2000` | Upstream connection timeout in milliseconds |
| `GATEWAY_RESPONSE_TIMEOUT` | `5s` | Upstream response timeout; Spring duration syntax is supported |

PowerShell example for a registry running on a different host:

```powershell
$env:EUREKA_SERVER_URL = "http://registry-host:8761/eureka/"
mvn spring-boot:run
```

## Errors

For a downstream connection failure, the gateway returns `502 Bad Gateway`; for an upstream timeout, it returns `504 Gateway Timeout`. Both include a JSON body with timestamp, status, error, and a safe message. Other unexpected errors are propagated rather than hidden as upstream errors. An unmatched path returns `404 Not Found`.

The gateway does not currently provide authentication, authorization, rate limiting, circuit breaking, or retries. Do not treat it as a security boundary until those policies and trusted network configuration are added.

## Tests

```powershell
mvn test
```

Routing integration tests start an ephemeral local HTTP backend and exercise the running gateway for each service route, including method/body/query forwarding and unmatched paths. Filter tests verify `502` and `504` error mapping. Tests do not require the microservices or Docker.
