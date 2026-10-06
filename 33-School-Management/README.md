# School Management

A Java 21 / Spring Boot 3 school API using Spring Data JPA with Hibernate ORM.
Students enroll in courses through an `Enrollment` entity so school-specific
data (enrollment date and grade) belongs to the relationship. Hibernate manages
the entity mappings, lazy associations, generated IDs, timestamps, and
optimistic-lock versions. DTOs keep persistence entities out of the API.

The default database is H2 in-memory; no database server is required and data
resets when the application stops.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`. The H2 console is available at
`http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:school`,
username `sa`, and an empty password.

## Endpoints

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/students` | Create a student | `201 Created` |
| `GET` | `/api/students` | List students | `200 OK` |
| `GET` | `/api/students/{id}` | Get a student and enrollments | `200 OK` |
| `PUT` | `/api/students/{id}` | Update a student | `200 OK` |
| `DELETE` | `/api/students/{id}` | Delete a student and enrollments | `204 No Content` |
| `GET` | `/api/students/{studentId}/enrollments` | List a student's enrollments | `200 OK` |
| `POST` | `/api/students/{studentId}/courses/{courseId}` | Enroll a student in a course | `201 Created` |
| `PUT` | `/api/students/{studentId}/enrollments/{enrollmentId}` | Set or update a grade | `200 OK` |
| `DELETE` | `/api/students/{studentId}/enrollments/{enrollmentId}` | Unenroll a student | `204 No Content` |
| `POST` | `/api/courses` | Create a course | `201 Created` |
| `GET` | `/api/courses` | List courses | `200 OK` |
| `GET` | `/api/courses/{id}` | Get a course and enrollments | `200 OK` |
| `PUT` | `/api/courses/{id}` | Update a course | `200 OK` |
| `DELETE` | `/api/courses/{id}` | Delete a course and enrollments | `204 No Content` |
| `GET` | `/api/courses/{courseId}/enrollments` | List a course's enrollments | `200 OK` |

Student emails are valid and unique; emails are normalized to lowercase.
Course codes are unique, normalized to uppercase, and contain 2-20 letters,
digits, or hyphens. Course credits must be between 1 and 6. Student birth dates
must be in the past. Grades are optional integers from 0 through 100. Invalid
requests return `400`, missing records return `404`, and duplicate emails,
course codes, or enrollments return `409`. Deleting a student or course removes
their enrollment records without cascading deletion to the other entity.

Student, Course, and Enrollment entities use Hibernate `@Version` optimistic
locking. If concurrent updates conflict, the API returns `409` and asks the
client to reload before retrying.

## Example

```bash
curl -i -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"Mina Patel","email":"mina@school.example","dateOfBirth":"2005-06-15"}'
curl -i -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{"code":"CS101","title":"Computer Science","credits":4}'
curl -i -X POST http://localhost:8080/api/students/1/courses/1
curl -i -X PUT http://localhost:8080/api/students/1/enrollments/1 \
  -H "Content-Type: application/json" \
  -d '{"grade":95}'
curl http://localhost:8080/api/courses/1/enrollments
```

## Test

```bash
mvn test
mvn package
```

H2 integration tests cover persistence, normalized unique identifiers,
enrollment/grade lifecycle, duplicate-enrollment protection, field validation,
missing records, and dependent enrollment cleanup on student or course removal.
