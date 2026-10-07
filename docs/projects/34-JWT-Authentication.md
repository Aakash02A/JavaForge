# Project Idea

Build a **JWT Authentication API** that lets users register, log in, receive a signed access token, and use that token to access protected resources.

# Problem Statement

An API needs a way to identify callers without asking users to send their password with every request or requiring the server to keep an HTTP session. This project introduces stateless authentication: the client exchanges credentials for a signed JSON Web Token (JWT), then presents the token to authenticate later requests.

# Objectives

- Create and authenticate user accounts.
- Hash passwords before storing them.
- Issue signed JWT access tokens after successful login.
- Validate bearer tokens on protected routes.
- Configure Spring Security for stateless HTTP requests.
- Return appropriate responses for invalid credentials and tokens.

# Features

- Register with email and password.
- Normalize account email addresses and enforce uniqueness.
- Hash passwords using BCrypt.
- Authenticate credentials and issue a time-limited token.
- Protect the current-account endpoint with bearer authentication.
- Reject malformed, invalid, expired, and missing tokens.
- Keep authentication data in an H2 database for local development.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA and Hibernate
- H2 in-memory database
- JJWT
- Bean Validation
- Maven

# Authentication Flow

1. A client posts an email and password to `/api/auth/register`.
2. The service normalizes the email, validates uniqueness, hashes the password with BCrypt, and stores the account.
3. The client posts credentials to `/api/auth/login`.
4. Spring Security verifies the submitted password against the stored hash.
5. On successful authentication, the API signs a JWT containing the account email as its subject and an expiration time.
6. The client sends the token in the `Authorization: Bearer <token>` header on a protected request.
7. A stateless security filter verifies the token signature and expiration, loads the account, and sets the authenticated principal for that request.

Passwords are never returned by the API. The JWT is an access credential, so clients should keep it private and send it only over HTTPS in deployed environments.

# Architecture

- **Controller layer:** maps registration, login, and account requests to HTTP.
- **Service layer:** handles email normalization, account creation, credential authentication, and token issuance.
- **Repository layer:** persists account entities and performs email lookups.
- **Security layer:** contains the password encoder, stateless security rules, JWT service, bearer-token filter, and unauthorized response handler.
- **DTO layer:** validates incoming values and shapes public responses without exposing password hashes.

The security policy permits only registration and login without a token. Other endpoints require a valid bearer token. HTTP sessions, form login, and HTTP Basic authentication are disabled.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The API is available at:

```text
http://localhost:8080
```

H2 is configured in memory, so no separate database server is required and registered users are removed when the application stops. For non-local use, configure `JWT_SECRET` with a private Base64-encoded signing key of at least 256 bits. The checked-in default is for local learning only and must not be used in a deployed environment.

# API Endpoints

| Method | Path | Description | Access |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Register a new account | Public |
| `POST` | `/api/auth/login` | Authenticate and receive a JWT | Public |
| `GET` | `/api/account/me` | Return the current authenticated account | Bearer token required |

Registration and login request payloads use these values:

```json
{
  "email": "asha@example.com",
  "password": "correct-horse-battery"
}
```

Registration passwords must contain between 12 and 72 characters. Emails must be valid, are normalized to lowercase, and are unique. Invalid data returns `400 Bad Request`, an existing account returns `409 Conflict`, and failed authentication returns `401 Unauthorized`. A missing, expired, or invalid bearer token also returns `401`.

The login response returns an `accessToken`, `tokenType` (`Bearer`), and `expiresInSeconds`. The default token lifetime is one hour.

# Example Requests

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

Replace `<accessToken>` with the `accessToken` value returned from the login response.

# Security Scope and Limitations

- The default key is a development convenience; configure a private key for any deployment.
- Use HTTPS when transmitting credentials or tokens outside local development.
- The database is in-memory and does not retain accounts after shutdown.
- This milestone covers authentication only; roles and fine-grained authorization are introduced in the next project.
- Refresh tokens, revocation, account recovery, and brute-force protections are outside this milestone's scope.

# Test

Run the automated integration tests and package the application:

```bash
mvn test
mvn package
```

Tests exercise registration and duplicate-email handling, password hashing, normalized email login, invalid credentials and payloads, JWT issuance, and access to a protected route with valid and invalid tokens.

# Project Details

Difficulty: ⭐⭐⭐⭐☆ (Advanced)

Estimated Time: 5–8 hours

Skills Gained:
- Spring Security configuration
- Stateless authentication
- JWT signing and validation
- BCrypt password hashing
- Bearer-token filters
- Authentication API design
