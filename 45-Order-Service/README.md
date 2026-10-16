# 45 - Order Service

A Spring Boot order microservice that stores customer orders and validates requested products against [44 - Product Service](../44-Product-Service/). Product name, SKU, and unit price are snapshotted into each order item so historical order details remain stable if the catalog changes.

## Features

- Create orders with one or more product lines.
- Validate products, prices, and available stock through Product Service.
- Store order records and item snapshots with Spring Data JPA.
- Calculate line totals and order totals using decimal arithmetic.
- List orders and retrieve a saved order by ID.
- Validate customer email, product IDs, quantities, and order size.
- Return consistent errors for missing products, insufficient stock, invalid input, and upstream outages.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Web (`RestClient`)
- Spring Cloud Netflix Eureka Client and LoadBalancer
- Spring Cloud Config Client
- Spring Data JPA and Hibernate
- H2 Database
- Maven
- JUnit 5, Mockito, and MockMvc

## Run locally

Start module 49 (Config Server) and module 48 (Service Discovery), then module 44 in separate terminals:

```powershell
cd 48-Service-Discovery
mvn spring-boot:run
```

```powershell
cd 44-Product-Service
mvn spring-boot:run
```

Start module 45:

```powershell
cd 45-Order-Service
mvn clean test
mvn spring-boot:run
```

The Product Service listens on port `8080`; the Order Service listens on `8081`. Both use separate in-memory H2 databases by default. Seed a product in the Product Service before submitting an order. Both load defaults from Config Server; Order Service locates Product Service through Eureka and Spring Cloud LoadBalancer.


## API

Order endpoints are served from `http://localhost:8081/api/orders`.

### Create an order

`POST /api/orders`

```json
{
  "customerEmail": "buyer@example.com",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

PowerShell example:

```powershell
$product = @{
  sku = "KB-001"
  name = "Mechanical Keyboard"
  description = "Compact wireless keyboard"
  price = 89.99
  stockQuantity = 25
} | ConvertTo-Json

$createdProduct = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/products `
  -ContentType "application/json" `
  -Body $product

$order = @{
  customerEmail = "buyer@example.com"
  items = @(@{ productId = $createdProduct.id; quantity = 2 })
} | ConvertTo-Json -Depth 5

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8081/api/orders `
  -ContentType "application/json" `
  -Body $order
```

An order response includes its ID, `CREATED` status, product snapshots, line totals, overall total, and creation timestamp.

### Retrieve an order

`GET /api/orders/{id}`

Returns a saved order, including its original item names, SKUs, and unit prices. Returns `404 Not Found` if the order ID does not exist.

### List orders

`GET /api/orders`

Returns all saved orders.

## Validation and errors

- Customer email must be valid and no longer than 254 characters.
- An order must contain between 1 and 50 items.
- Product IDs and quantities must be positive.
- A product may appear only once in an order.
- Requested quantity must not exceed the available stock reported by Product Service.

| Status | Meaning |
| --- | --- |
| `201 Created` | Order created |
| `200 OK` | Order retrieved or listed |
| `400 Bad Request` | Request body or validation failed, including duplicate product lines |
| `404 Not Found` | Order ID does not exist |
| `409 Conflict` | Requested quantity exceeds reported product stock |
| `422 Unprocessable Entity` | Product ID does not exist in Product Service |
| `503 Service Unavailable` | Product Service could not be reached |

Errors return a JSON object with timestamp, HTTP status, error label, and message.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8081` | Order Service HTTP port |
| `EUREKA_SERVER_URL` | `http://localhost:8761/eureka/` | Eureka registry URL |
| `EUREKA_CLIENT_ENABLED` | `true` | Enable Eureka registration and discovery |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Config Server base URL |
| `SPRING_CLOUD_CONFIG_ENABLED` | `true` | Enable Config Client |
| `PRODUCT_SERVICE_DISCOVERY_ENABLED` | `true` | Resolve Product Service using Eureka and LoadBalancer |
| `PRODUCT_SERVICE_URL` | `http://PRODUCT-SERVICE` | Product Service ID URI when discovery is enabled; direct URL when disabled |
| `DATABASE_URL` | `jdbc:h2:mem:orderdb` | Order Service JDBC connection URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | empty | Database password |

Example PowerShell configuration:

```powershell
$env:PRODUCT_SERVICE_URL = "http://localhost:8080"
$env:PRODUCT_SERVICE_DISCOVERY_ENABLED = "false"
$env:SERVER_PORT = "8081"
mvn spring-boot:run
```

## Architecture

```text
HTTP client
    |
    v
OrderController
    |
    v
OrderService -- validates catalog data and snapshots product details
    |                                   |
    v                                   v
OrderRepository                    ProductClient
    |                                   |
    v                                   v
H2 order database                Product Service (module 44)
```

## Tests

```powershell
mvn test
```

Integration tests use MockMvc and H2. The Product Service client is mocked, so tests need neither the Product Service nor network access. They cover order creation and totals, persisted snapshots, validation, stock checks, missing products, and missing orders.

## Limitations

- Product stock is checked but not reserved or decremented. Concurrent orders can therefore oversell stock; a production design needs an inventory reservation API or event-driven reservation with compensation.
- Product lookup and local order persistence do not form a distributed transaction. The Product Service must be reachable when an order is created.
- The default H2 database is in-memory. Use an external database and migrations for durable deployments.
- The service creates orders only; payment, cancellation, fulfillment, authentication, idempotency, and retry policies are outside this learning module.
