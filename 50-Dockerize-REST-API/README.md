# 50 - Dockerize REST API

Package a Spring Boot REST API as a small, non-root Docker image using a multi-stage build. The Maven build runs in a JDK image, while the final container contains only the JRE and executable application.

## Features

- Java 21 and Spring Boot 3 REST API.
- `GET /api/greeting` returns a JSON greeting.
- Optional `name` query parameter personalizes the greeting.
- Multi-stage Docker build separates compilation from runtime.
- Runs as an unprivileged user in the final image.
- `.dockerignore` keeps build output and local files out of the Docker context.

## Requirements

- JDK 21 or newer and Maven 3.6+ to run and test without Docker.
- Docker Engine/Desktop to build and run the container.

## Run locally

From this directory:

```bash
mvn spring-boot:run
```

The API listens on port `8080`. Try:

```bash
curl "http://localhost:8080/api/greeting"
curl "http://localhost:8080/api/greeting?name=Java%20Forge"
```

Responses:

```json
{"message":"Hello, world!"}
```

```json
{"message":"Hello, Java Forge!"}
```

## Build and run with Docker

Run these commands from this project directory:

```bash
docker build -t java-forge/dockerized-rest-api:1.0 .
docker run --rm -p 8080:8080 --name dockerized-rest-api java-forge/dockerized-rest-api:1.0
```

In another terminal, make a request:

```bash
curl "http://localhost:8080/api/greeting?name=container"
```

The container listens on port `8080`; `-p 8080:8080` publishes it on the host. Stop it with `Ctrl+C` (or `docker stop dockerized-rest-api` from another terminal).

## API

| Method | Path | Query parameter | Response |
| --- | --- | --- | --- |
| `GET` | `/api/greeting` | `name` (optional, defaults to `world`) | `200 OK`, JSON greeting |

Example response:

```json
{"message":"Hello, container!"}
```

## Tests

Run the controller tests without Docker:

```bash
mvn test
```

They verify the default and personalized HTTP responses. The Docker build packages the application with tests skipped because `mvn test` is the separate verification step.

## Project structure

```text
Dockerfile
.dockerignore
pom.xml
src/
  main/java/com/javaforge/dockerizedapi/
  test/java/com/javaforge/dockerizedapi/
```

## Learning objectives

- Write a multi-stage Dockerfile for a Maven/Spring Boot application.
- Separate build-time JDK dependencies from the runtime image.
- Publish an application port and launch an executable Spring Boot JAR.
- Exclude unnecessary local files from Docker build context.
- Run a Java service as a non-root container user.
