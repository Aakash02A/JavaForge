# 55 - Logging with ELK

Send structured Spring Boot application logs through Logstash to Elasticsearch, then inspect and search them in Kibana. Docker Compose starts the API and the three ELK services.

## Components and data flow

```text
POST /api/events
       |
       v
Spring Boot -- JSON over TCP --> Logstash -- Elasticsearch output --> Elasticsearch
       |                                                               |
       +-- JSON to container stdout                                   Kibana
```

- **Spring Boot:** writes one-line JSON logs to stdout and sends JSON log events to Logstash over TCP.
- **Logstash:** accepts JSON-lines events on port `5000` within the Compose network and indexes them in Elasticsearch.
- **Elasticsearch:** stores daily indices named `javaforge-logs-YYYY.MM.dd`.
- **Kibana:** provides the local interface to search the indexed events.

## Requirements

- Docker Engine/Desktop with Docker Compose.
- At least 4 GB of memory available to Docker is recommended for the ELK stack.
- JDK 21 and Maven 3.6+ only for running or testing the API without containers.

## Start the stack

From this project directory:

```bash
cp .env.example .env
docker compose up --build -d
docker compose ps
```

The API listens on `http://localhost:8080`. Kibana is bound to `127.0.0.1:5601` and can be opened at `http://localhost:5601`. Elasticsearch and Logstash are not published to host ports; services communicate over the private Compose network.

The Elasticsearch data directory is stored in the `elasticsearch_data` named volume.

## Send an event

Submit a log event to the API:

```bash
curl -i -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"message":"Payment completed for order 1234"}'
```

The API responds with `202 Accepted` and a generated event ID:

```json
{"eventId":"<generated-uuid>","status":"accepted"}
```

The log record includes a timestamp, level, logger name, message, and `event_id` MDC field. Requests with blank messages or messages over 500 characters return `400 Bad Request`.

## Search events in Kibana

1. Open `http://localhost:5601`.
2. Go to **Stack Management → Data Views** and create a data view with pattern `javaforge-logs-*`.
3. Select `@timestamp` as the time field.
4. Open **Discover**, choose the new data view, and search for the message or event ID.

Allow Logstash a short time to process the event and Elasticsearch to index it. The container logs are also useful when diagnosing startup:

```bash
docker compose logs -f api logstash elasticsearch kibana
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `API_PORT` | `8080` | Host port for the API |
| `KIBANA_PORT` | `5601` | Loopback-only host port for Kibana |
| `ELASTICSEARCH_MEMORY` | `1g` | Heap size used for both Elasticsearch `-Xms` and `-Xmx` |

Copy `.env.example` to `.env` to override these local settings. `.env` is ignored by Git.

For this local learning stack, Elasticsearch and Kibana security are disabled and data is not authenticated. Do not expose these services to an untrusted network or use this configuration for production. The Kibana host port is explicitly restricted to loopback; Elasticsearch and Logstash have no host port mappings.

## Run and test the API without Docker

The API writes structured logs to stdout. If Logstash is unavailable locally, the TCP appender retries while the console appender continues writing logs.

```bash
mvn spring-boot:run
```

Run the controller integration tests:

```bash
mvn test
```

Tests verify accepted events, generated IDs, and request validation without requiring Elasticsearch, Logstash, Kibana, or Docker.

## Stop and clean up

Stop the stack while retaining indexed data:

```bash
docker compose down
```

Remove containers and the Elasticsearch data volume:

```bash
docker compose down --volumes
```

## Project structure

```text
compose.yaml
Dockerfile
.dockerignore
.env.example
logstash/pipeline/logstash.conf
pom.xml
src/
  main/java/com/javaforge/elk/
  main/resources/application.properties
  main/resources/logback-spring.xml
  test/java/com/javaforge/elk/
```
