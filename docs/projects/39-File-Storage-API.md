# Project 39: File Storage API

## Project Idea

Build a real-world file-management API that accepts uploaded files, stores them safely on disk, keeps metadata in a database, and exposes routes to list, download, and delete them. The goal is to model a simplified file-service layer you can later extend into S3-backed cloud storage or document management systems.

## Problem Statement

Applications often need to handle user uploads such as profile pictures, invoices, reports, attachments, and documents. A file service must do more than save bytes; it must validate input, manage storage locations, keep metadata in a queryable database, and expose reliable download and delete workflows. This project solves that problem with a Spring Boot API using local file storage and H2 metadata persistence.

## Objectives

- Accept uploaded files through a REST API.
- Store each file in a dedicated local upload directory.
- Keep file metadata in a relational database.
- Return a generated download URL for each uploaded file.
- Allow listing of all stored files.
- Support downloading and deleting files by ID.
- Make the solution easy to test with MockMvc and an in-memory H2 database.

## Features

- `POST /api/files` uploads a file.
- `GET /api/files` lists all uploaded files.
- `GET /api/files/{id}` returns metadata for a single file.
- `GET /api/files/{id}/download` streams the upload bytes back to the client.
- `DELETE /api/files/{id}` removes the file from disk and the database.
- File names are sanitized to avoid unsafe path usage.
- Stored names are unique and timestamp-based to avoid collisions.
- Metadata includes filename, content type, file size, and creation time.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA and Hibernate
- H2 in-memory database
- Maven
- JUnit 5 and MockMvc

## Architecture

```text
FileStorageController
         |
         v
FileStorageService
         |
   +-----+------------------+
   |                         |
   v                         v
StoredFileRepository     Local filesystem uploads/
         |                         |
         v                         v
   H2 database              Stored files and bytes
```

The controller exposes the public API endpoints. The service handles validation, storage, and deletion logic, while the repository persists metadata so file lookup remains efficient and queryable.

## Upload Flow

1. The client sends a multipart file request to `/api/files`.
2. The service validates that the file exists and is not empty.
3. The original filename is sanitized to remove unsafe characters.
4. A unique stored filename is generated using a timestamp and cleaned base name.
5. The file bytes are copied into the local `uploads/` folder.
6. Metadata is persisted through JPA into the H2 database.
7. The API responds with the file metadata and download URL.

## Storage Strategy

This project intentionally stores files on the local filesystem instead of Amazon S3 or MinIO to keep the setup simple for learning. The system saves the binary content to disk while keeping a small metadata record in the database. This pattern is common in internal tooling and simple file services, and it mirrors the way larger object-storage systems behave conceptually.

The upload directory is managed by the application and created automatically if it does not exist. This keeps the app self-contained and makes local testing straightforward.

## Data Model

The `stored_files` table stores the following fields:

- `id`: generated primary key
- `file_name`: user-facing original filename
- `stored_file_name`: unique filename on disk
- `content_type`: MIME type of the uploaded file
- `size`: file size in bytes
- `created_at`: upload timestamp

Using a database table allows the service to query file metadata quickly, track file records consistently, and keep the file system and database in sync.

## API Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/files` | Upload a file |
| `GET` | `/api/files` | List uploaded files |
| `GET` | `/api/files/{id}` | Get metadata for one file |
| `GET` | `/api/files/{id}/download` | Download file bytes |
| `DELETE` | `/api/files/{id}` | Delete a file and its stored copy |

### Upload request

```bash
curl -i -X POST http://localhost:8080/api/files \
  -F "file=@/path/to/example.txt"
```

### Upload response

```json
{
  "id": 1,
  "fileName": "example.txt",
  "storedFileName": "1720000000000-example.txt",
  "contentType": "text/plain",
  "size": 128,
  "downloadUrl": "http://localhost:8080/api/files/1/download",
  "createdAt": "2026-10-12T12:00:00Z"
}
```

## Setup and Run

```bash
cd 39-File-Storage-API
mvn clean test
mvn package
java -jar target/39-file-storage-api-1.0-SNAPSHOT.jar
```

The app runs on `http://localhost:8080`. No external database is required because it uses H2 in memory. The upload directory is created automatically under the working directory as `uploads/`.

## Testing

The integration tests verify:

- upload success
- file listing
- file download content
- successful deletion
- not-found behavior after deletion

Run:

```bash
mvn test
```

## Production Considerations

This learner-friendly version stores files locally, which is simple and suitable for development but not ideal for production at scale. Real-world systems typically use object storage such as S3 or MinIO, add security scanning, enforce quotas, handle concurrency, and support asynchronous processing for large uploads. This project gives a solid foundation that can be extended into those environments.

## Learning Outcomes

- Design a multipart upload API with Spring Boot.
- Separate file metadata from file bytes.
- Use JPA entities and repository patterns for persistent metadata.
- Build filesystem-backed storage logic with safe path handling.
- Create a clean API contract for listing, downloading, and deleting files.
- Validate upload workflows end-to-end with MockMvc integration tests.
