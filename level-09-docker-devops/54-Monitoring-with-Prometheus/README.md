# 54 - Monitoring with Prometheus

Instrument a Spring Boot REST API with Micrometer, expose application metrics through Spring Boot Actuator, and scrape them with Prometheus. Docker Compose runs the API and Prometheus as separate services.

## Features

- Java 21 and Spring Boot 3.
- `GET /api/greeting` endpoint with a custom `javaforge.greetings` counter.
- Actuator health endpoint at `/actuator/health`.
- Prometheus-formatted metrics at `/actuator/prometheus`.
- Prometheus scrape configuration with a five-second interval.
- Compose-managed services and a named volume for Prometheus data.
- Prometheus UI bound to host loopback by default.

## Requirements

- Docker Engine/Desktop with Docker Compose for the full monitoring stack.
- JDK 21 and Maven 3.6+ to run and test without Docker.

## Run with Docker Compose

From this project directory, optionally copy the environment template:

```bash
cp .env.example .env
```

Build the API image and start both services:

```bash
docker compose up --build -d
docker compose ps
```

The REST API is available at `http://localhost:8080` and the Prometheus UI at `http://localhost:9090`. Prometheus's host port is bound to `127.0.0.1`; it is not published on external network interfaces. To change host ports, set `API_PORT` or `PROMETHEUS_PORT` in `.env`.

## Generate metrics

Call the API a few times to increment its request counter:

```bash
curl "http://localhost:8080/api/greeting"
curl "http://localhost:8080/api/greeting?name=Java%20Forge"
```

Example response:

```json
{"message":"Hello, Java Forge!"}
```

Check health and raw Prometheus metrics:

```bash
curl "http://localhost:8080/actuator/health"
curl "http://localhost:8080/actuator/prometheus"
```

In the Prometheus UI, query:

```promql
javaforge_greetings_total
```

Other useful starter queries:

```promql
rate(javaforge_greetings_total[1m])
up{job="spring-boot-api"}
```

Prometheus scrapes `api:8080` over the Compose network using `/actuator/prometheus`; the service name `api` is not the host's `localhost`.

## Configuration and security

Only the health and Prometheus Actuator endpoints are exposed over HTTP. Health responses do not include component details. The Prometheus UI binds to loopback by default; avoid exposing it publicly without appropriate authentication and network controls. Metrics can reveal operational information, so review the metric set before exposing the scrape endpoint outside a trusted network.

Prometheus stores its time series in the `prometheus_data` named volume. Stop services and preserve data with:

```bash
docker compose down
```

Remove the stack and its stored metrics with:

```bash
docker compose down --volumes
```

## Run and test without Docker

Start the API:

```bash
mvn spring-boot:run
```

Run the integration tests:

```bash
mvn test
```

The tests start the application on a random local port, verify both HTTP endpoints, and confirm the custom counter appears in the Prometheus scrape output. Docker and Prometheus are not needed to run the tests.

## Project structure

```text
compose.yaml
Dockerfile
.dockerignore
.env.example
prometheus/prometheus.yml
pom.xml
src/
  main/java/com/javaforge/monitoring/
  main/resources/application.properties
  test/java/com/javaforge/monitoring/
```

## Learning objectives

- Instrument application behavior with Micrometer counters.
- Expose health and metrics via Spring Boot Actuator.
- Configure Prometheus scrape jobs and targets.
- Use Docker Compose service DNS and persistent named volumes.
- Query counters and scrape health with PromQL.
