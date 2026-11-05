# 46 - Payment Service

A Spring Boot payment API that stores completed payment records and publishes `payment.completed` events to RabbitMQ. Payment creation and its event are saved together in a transactional outbox, so an unavailable broker does not lose the event.

## Features

- Create one payment for an order and retrieve it by payment ID or order ID.
- Validate order ID, amount, currency, and payment method.
- Prevent duplicate payments for the same order.
- Persist payment records and outgoing events in H2 using Spring Data JPA.
- Publish events through a durable RabbitMQ direct exchange.
- Retry pending outbox events after broker failures; delivery is at-least-once.
- Return consistent JSON errors for invalid requests, duplicate payments, and missing payments.

## Payment behavior

This learning module records the payment as `COMPLETED`; it does not integrate with a real payment gateway or handle card data. Do not send card numbers or security codes as `paymentMethod`. A production integration must use a payment provider's tokenized payment method and model authorization, capture, decline, refund, and reconciliation states explicitly.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Web, Validation, Data JPA, and AMQP
- Spring Cloud Netflix Eureka Client
- Spring Cloud Config Client
- H2 Database
- RabbitMQ 4
- Maven, JUnit 5, Mockito, and MockMvc

## Run locally

From the repository root, start Config Server and Service Discovery:

```powershell
cd 49-Config-Server
mvn spring-boot:run
```

```powershell
cd 48-Service-Discovery
mvn spring-boot:run
```

In another terminal, start RabbitMQ:

```powershell
cd 46-Payment-Service
docker compose up -d
```

Run tests and start the API from the Payment Service directory:

```powershell
mvn clean test
mvn spring-boot:run
```

The Payment Service listens on `http://localhost:8082`, loads defaults from Config Server at `http://localhost:8888`, and registers with Eureka at `http://localhost:8761/eureka/` by default. Configure `CONFIG_SERVER_URL` and `EUREKA_SERVER_URL` to use different servers, or set `SPRING_CLOUD_CONFIG_ENABLED=false` and `EUREKA_CLIENT_ENABLED=false` to disable clients. RabbitMQ management is available at `http://localhost:15672` (development credentials default to `guest`/`guest`). Stop RabbitMQ using `docker compose down`.

## API

### Create a payment

`POST /api/payments`

```json
{
  "orderId": 42,
  "amount": 129.50,
  "currency": "USD",
  "paymentMethod": "card"
}
```

Returns `201 Created` with the persisted payment, including its generated ID, `COMPLETED` status, and timestamp. A second payment for the same order returns `409 Conflict`.

PowerShell example:

```powershell
$payment = @{
  orderId = 42
  amount = 129.50
  currency = "USD"
  paymentMethod = "card"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8082/api/payments `
  -ContentType "application/json" `
  -Body $payment
```

### Retrieve a payment

- `GET /api/payments/{id}` — find by payment ID.
- `GET /api/payments/order/{orderId}` — find by order ID.

Unknown IDs return `404 Not Found`.

## Payment events

Each successful payment creates an outbox row in the same database transaction as the payment record. A scheduled publisher sends pending rows to the durable `payment.events` direct exchange using routing key `payment.completed`. The message body contains `paymentId`, `orderId`, `amount`, `currency`, `status`, and `occurredAt`.

The publisher marks an outbox row as delivered after RabbitMQ accepts the send. If publishing fails, the row remains pending and the scheduler retries it. Because a crash can happen after RabbitMQ accepts a message but before the database marks it delivered, consumers must be idempotent; delivery is at-least-once, not exactly-once. This module declares the exchange but leaves queue creation and consumer behavior to downstream services.

Tests disable the scheduler and use mocked publisher dependencies; no RabbitMQ or Docker is required to run them.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8082` | HTTP port |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Config Server base URL |
| `SPRING_CLOUD_CONFIG_ENABLED` | `true` | Enable Config Client |
| `EUREKA_SERVER_URL` | `http://localhost:8761/eureka/` | Eureka registry URL |
| `EUREKA_CLIENT_ENABLED` | `true` | Enable Eureka registration and discovery |
| `DATABASE_URL` | `jdbc:h2:mem:paymentdb` | JDBC URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | empty | Database password |
| `RABBITMQ_HOST` | `localhost` | RabbitMQ hostname |
| `RABBITMQ_PORT` | `5672` | RabbitMQ port |
| `RABBITMQ_USERNAME` | `guest` | RabbitMQ username |
| `RABBITMQ_PASSWORD` | `guest` | RabbitMQ password |
| `PAYMENT_EXCHANGE` | `payment.events` | Direct exchange for payment events |
| `OUTBOX_PUBLISH_INTERVAL` | `1000` | Delay between pending event publication runs, in milliseconds |

## Errors

| Status | Meaning |
| --- | --- |
| `201 Created` | Payment created |
| `400 Bad Request` | Invalid or malformed request |
| `404 Not Found` | Payment does not exist |
| `409 Conflict` | A payment already exists for the order |

Errors are JSON objects with a timestamp, HTTP status, error label, and message.

## Tests

```powershell
mvn test
```

Integration tests use MockMvc and an in-memory H2 database. They cover payment creation, transactional outbox persistence, duplicate-order rejection, validation, lookup, and not-found handling. Publisher tests verify successful delivery marking and retry behavior without a live broker.
