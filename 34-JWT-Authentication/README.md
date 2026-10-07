# JWT Authentication

A Java 21 / Spring Boot 3 authentication API demonstrating stateless JSON Web
Tokens with Spring Security. Accounts are stored with Spring Data JPA in an H2
in-memory database. Passwords are hashed with BCrypt; raw passwords are never
stored or returned.

This milestone focuses on authentication. Role-based authorization and refresh
tokens are left for the following security projects.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`. The default JWT signing key is for
local learning only. Set a private Base64-encoded key through `JWT_SECRET`
before using this project outside local development. For example, generate a
key with:

```bash
openssl rand -base64 32
```

The database is in-memory; account data is cleared when the application stops.

## Endpoints

| Method | Path | Description | Access |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Create an account | Public |
| `POST` | `/api/auth/login` | Verify credentials and issue a JWT | Public |
| `GET` | `/api/account/me` | Get the authenticated account | Bearer token |

Registration requires a valid email and a password from 12 to 72 characters.
Emails are trimmed, normalized to lowercase, and unique. Passwords are hashed
with BCrypt. Duplicate emails return `409`; invalid payloads return `400`.
Incorrect login credentials and missing or invalid access tokens return `401`.

The access token is signed with HS256 and expires after one hour by default.
Send it as `Authorization: Bearer <accessToken>` when accessing protected
resources. This API does not implement refresh tokens or role-based access.

## Example

```bash
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"asha@example.com","password":"correct-horse-battery"}'

curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"asha@example.com","password":"correct-horse-battery"}'

curl -i http://localhost:8080/api/account/me \
  -H "Authorization: Bearer <accessToken>"
```

The login response contains `accessToken`, `tokenType`, and `expiresInSeconds`.
Copy the token from that response for the protected request.

## Test and Build

```bash
mvn test
mvn package
```

Integration tests verify password hashing, duplicate registration, email
normalization, credential failures, token issuance, protected endpoint access,
invalid token rejection, and request validation.
