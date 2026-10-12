# 39 - File Storage API

A Spring Boot REST API for uploading, listing, downloading, and deleting files from local disk storage with database metadata tracking.

## Features

- Upload files with original names and MIME types
- Store files in a local uploads directory
- Persist metadata in an H2 database
- List all uploaded files and view storage details
- Download a file by ID or filename
- Delete files and remove physical storage entries
- Validate file presence and avoid duplicate uploads by generated unique IDs

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA and Hibernate
- H2 in-memory database
- Maven
- JUnit 5 and MockMvc

## Run

```bash
cd 39-File-Storage-API
mvn clean test
mvn package
java -jar target/39-file-storage-api-1.0-SNAPSHOT.jar
```

The service runs at `http://localhost:8080` and uses an in-memory H2 database. No external database is required.

## API

### Upload a file

```bash
curl -i -X POST http://localhost:8080/api/files \
  -F "file=@/path/to/example.txt"
```

Example response:

```json
{
  "id": 1,
  "fileName": "example.txt",
  "storedFileName": "1-example.txt",
  "contentType": "text/plain",
  "size": 128,
  "downloadUrl": "http://localhost:8080/api/files/1/download",
  "createdAt": "2026-10-12T12:00:00Z"
}
```

### List files

```bash
curl -i http://localhost:8080/api/files
```

### Download a file

```bash
curl -i http://localhost:8080/api/files/1/download
```

### Delete a file

```bash
curl -i -X DELETE http://localhost:8080/api/files/1
```

## Notes

- Files are stored in the local `uploads/` directory relative to the application working directory.
- The API stores file metadata in the database to support lookup and admin actions.
- This project is intentionally local-file based instead of cloud object storage, keeping it simple for learning and testing.

## Tests

```bash
mvn test
```

The test suite validates upload success, file listing, download access, and delete operations.
