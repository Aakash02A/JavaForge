# 32 - Hospital API

Build a **Hospital API** that tracks doctors and patients and manages many-to-many assignments through a join table.

# Problem Statement

In healthcare systems, one doctor can treat many patients and one patient can see many doctors. This kind of relationship cannot be modeled with a single foreign key on one side. It requires a join table and careful synchronization when records are updated or deleted. This project teaches many-to-many JPA mapping in a realistic domain.

# Objectives

- Model doctors and patients in a many-to-many relationship.
- Persist assignments using a join table.
- Create endpoints for managing both sides of the relationship.
- Normalize and validate emails and dates.
- Practice relationship cleanup and conflict handling.

# Features

- Create doctors and patients.
- Assign a patient to a doctor.
- Remove a doctor-patient assignment.
- List doctors and their patients.
- List patients and their doctors.
- Update doctor and patient records.
- Delete doctor or patient records and their assignments.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate ORM
- H2 in-memory database
- Maven

# Architecture and Flow

The project uses a standard many-to-many design:

- `Doctor` can be linked to many `Patient` entries.
- `Patient` can be linked to many `Doctor` entries.
- A join table stores the relationship.
- DTOs keep API payloads compact and readable.

This is a classic example of a relationship that requires updates on both sides to stay consistent when the association changes.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The API listens on:

```text
http://localhost:8080
```

The H2 database console is provided at:

```text
http://localhost:8080/h2-console
```

with JDBC URL:

```text
jdbc:h2:mem:hospital
```

and username `sa`.

# API Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/doctors` | Create a doctor | `201 Created` |
| `GET` | `/api/doctors` | List doctors | `200 OK` |
| `GET` | `/api/doctors/{id}` | Get one doctor | `200 OK` |
| `PUT` | `/api/doctors/{id}` | Update a doctor | `200 OK` |
| `DELETE` | `/api/doctors/{id}` | Delete a doctor and assignments | `204 No Content` |
| `POST` | `/api/patients` | Create a patient | `201 Created` |
| `GET` | `/api/patients` | List patients | `200 OK` |
| `GET` | `/api/patients/{id}` | Get one patient | `200 OK` |
| `PUT` | `/api/patients/{id}` | Update a patient | `200 OK` |
| `DELETE` | `/api/patients/{id}` | Delete a patient and assignments | `204 No Content` |
| `POST` | `/api/doctors/{doctorId}/patients/{patientId}` | Assign a patient to a doctor | `200 OK` |
| `DELETE` | `/api/doctors/{doctorId}/patients/{patientId}` | Remove an assignment | `200 OK` |

Validation rules:

- Names and specialties are required.
- Emails must be valid and unique within the entity type.
- Patient date of birth must be in the past.
- Duplicate email values return `409 Conflict`.
- Missing records or assignments return `404 Not Found`.
- Invalid payloads return `400 Bad Request`.

# Example Requests

```bash
curl -i -X POST http://localhost:8080/api/doctors \
  -H "Content-Type: application/json" \
  -d '{"name":"Dr. Maya Rao","email":"maya@hospital.example","specialty":"Cardiology"}'

curl -i -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"Ari Kim","email":"ari@patient.example","dateOfBirth":"1990-06-15"}'

curl -i -X POST http://localhost:8080/api/doctors/1/patients/1
curl http://localhost:8080/api/patients/1
curl -i -X DELETE http://localhost:8080/api/doctors/1/patients/1
```

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 4–6 hours

Skills Gained:
- Many-to-many JPA mapping
- Join table design
- Relationship cleanup logic
- DTO design for linked records
- Conflict handling in relational APIs

## Tests

Run `mvn test` to execute `HospitalApiIntegrationTest`. This integration test exercises doctor/patient API operations and their many-to-many relationship using the configured test database.
