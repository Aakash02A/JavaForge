# Hospital API

A Java 21 / Spring Boot 3 REST API demonstrating a bidirectional many-to-many
Spring Data JPA relationship. Doctors can care for many patients, and patients
can be assigned to many doctors. A join table persists each assignment; DTOs
return compact related-record summaries without recursive entity serialization.

The project uses an H2 in-memory database by default, so no database server is
needed. Data resets when the application stops.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`. The H2 console is available at
`http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:hospital`,
username `sa`, and an empty password.

## Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/doctors` | Create a doctor | `201 Created` |
| `GET` | `/api/doctors` | List doctors and assigned patients | `200 OK` |
| `GET` | `/api/doctors/{id}` | Get a doctor and assigned patients | `200 OK` |
| `PUT` | `/api/doctors/{id}` | Update a doctor | `200 OK` |
| `DELETE` | `/api/doctors/{id}` | Delete a doctor and its assignments | `204 No Content` |
| `POST` | `/api/patients` | Create a patient | `201 Created` |
| `GET` | `/api/patients` | List patients and assigned doctors | `200 OK` |
| `GET` | `/api/patients/{id}` | Get a patient and assigned doctors | `200 OK` |
| `PUT` | `/api/patients/{id}` | Update a patient | `200 OK` |
| `DELETE` | `/api/patients/{id}` | Delete a patient and its assignments | `204 No Content` |
| `POST` | `/api/doctors/{doctorId}/patients/{patientId}` | Assign a patient to a doctor | `200 OK` |
| `DELETE` | `/api/doctors/{doctorId}/patients/{patientId}` | Remove an assignment | `200 OK` |

Names and specialties are required (up to 100 characters), and email addresses
must be valid and unique within their record type. Patient dates of birth must
be in the past. Invalid input returns `400`; missing records or assignments
return `404`; duplicate emails return `409`. Email addresses are normalized to
lowercase.

## Example

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

## Test

```bash
mvn test
mvn package
```

H2 integration tests cover doctors and patients sharing many-to-many
assignments, both sides of relationship retrieval, updates, validation,
duplicate email protection, missing records, and join-row cleanup on deletion.
