# 51 - Docker Compose

Run a Spring Boot Notes REST API and PostgreSQL together as a multi-container application. Docker Compose builds the API image, waits until the database health check passes, connects the services on the Compose network, and stores database files in a named volume.

## Features

- Java 21 and Spring Boot REST API with note CRUD operations.
- PostgreSQL database for persistent note storage.
- Compose-managed API and database services.
- Database health check and startup dependency ordering.
- Named volume to retain PostgreSQL data across container restarts.
- Multi-stage API image that runs as a non-root user.
- Local `.env` configuration excluded from Git and Docker build context.

## Requirements

- Docker Engine/Desktop with the Docker Compose plugin.
- JDK 21 and Maven 3.6+ only if running or testing the API outside Docker.

## Configure and start

From this project directory, create a local environment file from the example:

```powershell
Copy-Item .env.example .env
```

Change `POSTGRES_PASSWORD` in `.env` before sharing or using this example beyond a local learning environment. The `.env` file is ignored by Git.

Build and start both services:

```bash
docker compose up --build -d
docker compose ps
```

Compose starts PostgreSQL first and waits for its health check before starting the API. The API is available at `http://localhost:8080` by default. Set `APP_PORT` in `.env` to change the host port.

## Try the API

Create a note:

```bash
curl -i -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title":"Docker Compose","content":"The API and database run as separate services."}'
```

List notes and fetch an individual note:

```bash
curl http://localhost:8080/api/notes
curl http://localhost:8080/api/notes/1
```

Update and delete a note:

```bash
curl -i -X PUT http://localhost:8080/api/notes/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated title","content":"Updated content"}'

curl -i -X DELETE http://localhost:8080/api/notes/1
```

The generated IDs are database-assigned; use the ID returned by `POST` for subsequent requests.

## API endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/notes` | Create a note | `201 Created` |
| `GET` | `/api/notes` | List notes | `200 OK` |
| `GET` | `/api/notes/{id}` | Fetch a note | `200 OK` |
| `PUT` | `/api/notes/{id}` | Replace a note | `200 OK` |
| `DELETE` | `/api/notes/{id}` | Delete a note | `204 No Content` |

Titles are required and limited to 120 characters. Content is optional and limited to 2,000 characters. Invalid requests return `400 Bad Request`; unknown IDs return `404 Not Found`.

## Service configuration

| Service | Purpose | Internal port |
| --- | --- | --- |
| `api` | Spring Boot REST API | `8080` |
| `db` | PostgreSQL | `5432` |

The API connects to the database using the Compose service name `db`, not `localhost`. `postgres_data` is a named volume mounted at PostgreSQL's data directory. The API and database credentials are configured using `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` in `.env`.

Inspect service logs:

```bash
docker compose logs -f api
docker compose logs -f db
```

Stop the services while retaining database data:

```bash
docker compose down
```

Remove the services and their database volume (this deletes saved notes):

```bash
docker compose down --volumes
```

## Run and test without Docker

The Maven tests use an in-memory H2 database, so they do not require Docker or PostgreSQL:

```bash
mvn test
```

To run the API locally, configure the `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` environment variables for a reachable PostgreSQL instance, then run:

```bash
mvn spring-boot:run
```

## Project structure

```text
compose.yaml
Dockerfile
.dockerignore
.env.example
pom.xml
src/
  main/java/com/javaforge/composeapi/
  main/resources/application.properties
  test/java/com/javaforge/composeapi/
  test/resources/application.properties
```

## Learning objectives

- Define cooperating services, networks, ports, and a named volume in Compose.
- Configure service-to-service networking with DNS service names.
- Wait for database readiness with a health check and `depends_on`.
- Keep local configuration out of version control.
- Run and test a database-backed API locally and in containers.
