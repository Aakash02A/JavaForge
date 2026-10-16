# 48 - Service Discovery

A Spring Cloud Netflix Eureka server that provides a registry for the Product, Order, Payment, and API Gateway applications. Services register their application names and network locations; the gateway resolves service instances from this registry instead of requiring fixed backend URLs.

## Features

- Eureka server with a web dashboard and registry API.
- Registry runs standalone and does not register itself as a client.
- Existing modules 44–47 register as Eureka clients.
- API Gateway routes by service ID with Spring Cloud LoadBalancer.
- Configure server URL and service registration using environment variables.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Cloud 2025.0.0
- Spring Cloud Netflix Eureka Server and Client
- Maven and JUnit 5

## Start the registry

From this directory:

```powershell
mvn clean test
mvn spring-boot:run
```

The Eureka dashboard is at `http://localhost:8761/`; the registry API is at `http://localhost:8761/eureka/apps`. Start the registry before starting service instances. Registration and health status may take a short period to appear as Eureka clients send heartbeats.

## Run the microservices with discovery

With the registry running, start modules 44, 45, 46, and 47 in separate terminals. Their defaults connect to `http://localhost:8761/eureka/`. The API Gateway resolves:

| Service ID | Module | API prefix |
| --- | --- | --- |
| `PRODUCT-SERVICE` | [44 - Product Service](../44-Product-Service/) | `/api/products` |
| `ORDER-SERVICE` | [45 - Order Service](../45-Order-Service/) | `/api/orders` |
| `PAYMENT-SERVICE` | [46 - Payment Service](../46-Payment-Service/) | `/api/payments` |

Send requests through the gateway at `http://localhost:8083`. The gateway uses `lb://` routes and Spring Cloud LoadBalancer to choose a registered instance. The downstream services can also be run directly on their existing ports.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8761` | Eureka server HTTP port |
| `EUREKA_EMPTY_SYNC_WAIT_MS` | `0` | Empty-registry startup wait interval |
| `EUREKA_SERVER_URL` | `http://localhost:8761/eureka/` | Eureka registry URL used by modules 44–47 |
| `EUREKA_CLIENT_ENABLED` | `true` | Enable registration and discovery in modules 44–47 |

To point clients at a registry on another host, set `EUREKA_SERVER_URL` in each service and the gateway process. Set `EUREKA_CLIENT_ENABLED=false` to run those applications standalone without contacting Eureka; the gateway then needs its upstream URL settings configured to direct service URLs. For standalone Order Service calls, also set `PRODUCT_SERVICE_DISCOVERY_ENABLED=false` and `PRODUCT_SERVICE_URL` to the direct Product Service URL.

## Registry API and operations

The dashboard lists registered applications and instance health. The standard Eureka API is exposed under `/eureka`; for example, `GET /eureka/apps` returns the registered applications. Eureka is intended for a trusted development network in this module; it is not configured with authentication, TLS, replication, or production-grade persistence.

## Tests

```powershell
mvn test
```

Integration tests start the Eureka server on an ephemeral port and verify the registry endpoint and dashboard. They do not require the other modules or Docker.
