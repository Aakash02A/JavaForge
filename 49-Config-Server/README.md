# 49 - Config Server

A Spring Cloud Config Server that centralizes runtime configuration for modules 44–47. It serves application-specific properties from a packaged native repository, so this learning setup is self-contained and does not require a separate Git repository.

## Features

- Expose the Spring Cloud Config REST API.
- Provide separate configuration for Product, Order, Payment, and API Gateway applications.
- Store sample configuration in a native, classpath-backed repository.
- Keep environment-variable placeholders for host-specific values and secrets.
- Allow client applications to retain their local properties as a fallback if the server is unavailable.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Cloud 2025.0.0
- Spring Cloud Config Server and Config Client
- Maven and JUnit 5

## Start the server

From this directory:

```powershell
mvn clean test
mvn spring-boot:run
```

The server listens on `http://localhost:8888`. Start Config Server before the Eureka registry and services so clients can load their centralized properties at startup. Config Server imports are optional, allowing a service to start from its local configuration when the server is not running.

## Config API

Spring Cloud Config exposes environment data at `/{application}/{profile}`:

```powershell
Invoke-RestMethod http://localhost:8888/product-service/default
Invoke-RestMethod http://localhost:8888/order-service/default
Invoke-RestMethod http://localhost:8888/payment-service/default
Invoke-RestMethod http://localhost:8888/api-gateway/default
```

The configuration source files are under `src/main/resources/configurations/`. Add or update `<application>.properties` there and restart the server to include changes from the packaged repository. These files are suitable for non-secret defaults only; keep credentials in environment variables or a secured production configuration backend.

## Run with the microservices

1. Start module 49 Config Server.
2. Start module 48 Service Discovery.
3. Start modules 44 Product Service, 45 Order Service, 46 Payment Service, and 47 API Gateway.

Modules 44–47 import Config Server properties by application name with profile `default`. Their existing local settings remain available if Config Server cannot be contacted. Set `CONFIG_SERVER_URL` to use a different server, or set `SPRING_CLOUD_CONFIG_ENABLED=false` to disable the Config Client. To keep local integration tests independent of other services, their test configuration explicitly disables Config Client and Eureka discovery.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8888` | Config Server HTTP port |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Config Server base URL used by client modules |
| `SPRING_CLOUD_CONFIG_ENABLED` | `true` | Enable Config Client in modules 44–47 |

The server uses Spring Cloud's `native` profile and searches `classpath:/configurations`. Do not place passwords, access tokens, or other secrets in the packaged repository.

An unknown application name may return `200 OK` with an empty `propertySources` array; clients should verify that requested configuration is present when required. Because the repository is packaged on the server classpath, changes require rebuilding/restarting the server. Use a secured external Git repository or shared backend for dynamic refresh and multi-instance deployments.

## Tests

```powershell
mvn test
```

Integration tests run the server on an ephemeral port and verify application configuration endpoints and unknown-application handling. They require neither Eureka nor Docker.
