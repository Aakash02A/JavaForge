# 37 - Refresh Token

## Project Idea

Extend JWT authentication with a secure refresh-token lifecycle. Access tokens should be short-lived to limit exposure, while users should be able to obtain a new access token without submitting their password again.

## Problem Statement

A long-lived access token is convenient but increases the damage caused by accidental disclosure. A very short-lived access token improves security but would force users to log in repeatedly unless a second credential can renew it. This project implements that second credential as a stored, revocable, rotating refresh token.

## Objectives

- Keep JWT access tokens short-lived.
- Issue cryptographically random refresh tokens.
- Store only a one-way hash of each refresh token.
- Rotate refresh tokens after every successful use.
- Detect replay of an already-used refresh token.
- Revoke a refresh-token family when replay is detected.
- Support explicit logout and test the complete lifecycle.

## Features

- Registration with normalized email and BCrypt password hashing.
- Login returning a 15-minute access token and 30-day refresh token.
- Persistent refresh-token records in H2/JPA.
- Refresh endpoint with rotation.
- Reuse detection and family-wide revocation.
- Logout endpoint for refresh-token revocation.
- Protected account endpoint using the JWT bearer token.
- Structured validation and unauthorized responses.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA and Hibernate
- H2 in-memory database
- JJWT 0.12.6
- Jakarta Bean Validation
- Maven
- JUnit 5, Spring Boot Test, and MockMvc

## Architecture

```text
AuthController
  -> AuthService
       -> AppUserRepository
       -> RefreshTokenRepository
       -> JwtService
       -> PasswordEncoder

JwtAuthenticationFilter -> protected controllers
```

`AuthService` handles credential authentication and refresh-token lifecycle operations. `JwtService` signs and validates access tokens. `RefreshToken` is a database entity that contains the token hash, owning email, token family, expiry, revocation time, and replacement relationship. The raw refresh token is returned only at issuance time.

## Authentication Flow

### Login

1. The client submits email and password.
2. Spring Security verifies the BCrypt password hash.
3. The API creates a short-lived JWT.
4. The API generates a random opaque refresh token.
5. Only the refresh token's SHA-256 hash is stored.
6. Both credentials are returned to the client.

### Refresh

1. The client submits the refresh token to `/api/auth/refresh`.
2. The API hashes the presented value and looks up the record.
3. An active token is revoked immediately.
4. A replacement token is created in the same family.
5. A new access/refresh pair is returned.

### Replay detection

If a previously rotated or logged-out refresh token is submitted again, the request returns `401 Unauthorized` and all still-active records in that token family are revoked. This prevents an attacker who obtained an old token from continuing to refresh a session.

## Data Model

The `refresh_tokens` table stores:

- `token_hash`: SHA-256 hash of the opaque token.
- `user_email`: account associated with the token.
- `family_id`: groups a login session and its rotated replacements.
- `created_at` and `expires_at`: token lifetime.
- `revoked_at`: logout, rotation, or family-revocation timestamp.
- `replaced_by_hash`: hash of the next rotated token when applicable.

Raw refresh tokens are never persisted.

## API Endpoints

| Method | Path | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Public | Create an account |
| `POST` | `/api/auth/login` | Public | Issue an access/refresh pair |
| `POST` | `/api/auth/refresh` | Public | Rotate a valid refresh token |
| `POST` | `/api/auth/logout` | Public | Revoke a submitted refresh token |
| `GET` | `/api/account/me` | Bearer JWT | Read the current account |

## Example Requests

```bash
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"asha@example.com","password":"correct-horse-battery"}'

curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"asha@example.com","password":"correct-horse-battery"}'

curl -i -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken>"}'

curl -i -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken>"}'
```

The response from login and refresh includes `accessToken`, `tokenType`, `expiresInSeconds`, `refreshToken`, and `refreshTokenExpiresInSeconds`.

## Setup and Run

```bash
cd 37-Refresh-Token
mvn clean test
mvn package
java -jar target/37-refresh-token-1.0-SNAPSHOT.jar
```

The default H2 database is in memory and requires no separate database server. Set `JWT_SECRET` outside local learning environments:

```powershell
$env:JWT_SECRET = "<base64-encoded-random-key>"
```

## Testing

The automated tests verify:

- Login issues both credential types with the expected lifetimes.
- Refresh rotates the token and leaves the previous token unusable.
- Replay of an old token revokes the complete family.
- Logout prevents later refresh.
- The access token authorizes `/api/account/me`.

Run:

```bash
mvn test
mvn package
```

## Security Notes and Limitations

- Access tokens remain valid until their short expiration; logout revokes refresh capability, not already-issued JWTs.
- H2 in-memory storage is for learning and local testing only.
- Production deployments should use a durable database, HTTPS, key rotation, rate limiting, device/session management, and secure client-side token storage.
- Refresh-token cleanup for expired records is not automated in this milestone.

## Learning Outcomes

- Distinguish access-token and refresh-token responsibilities.
- Implement opaque-token hashing and persistence.
- Understand rotation, replay detection, and token-family revocation.
- Design authentication endpoints with clear failure semantics.
- Test stateful security behavior through integration tests.
