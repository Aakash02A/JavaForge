# 37 - Refresh Token

A Spring Boot authentication API that combines short-lived JWT access tokens with persistent, rotating refresh tokens.

## Features

- User registration with BCrypt password hashing.
- Fifteen-minute JWT access tokens.
- Thirty-day opaque refresh tokens.
- Refresh-token hashes stored in the database instead of raw tokens.
- One-time refresh-token rotation.
- Refresh-token reuse detection that revokes the entire token family.
- Explicit logout that revokes the submitted refresh token.
- H2 in-memory persistence for local learning.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA and Hibernate
- H2
- JJWT 0.12.6
- Maven
- JUnit 5 and MockMvc

## Run

```bash
cd 37-Refresh-Token
mvn clean test
mvn package
java -jar target/37-refresh-token-1.0-SNAPSHOT.jar
```

The API starts at `http://localhost:8080`. No external database is required. For deployment, override `JWT_SECRET` with a private Base64-encoded key of at least 256 bits.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/auth/register` | Create an account |
| POST | `/api/auth/login` | Return access and refresh tokens |
| POST | `/api/auth/refresh` | Rotate a refresh token and issue a new pair |
| POST | `/api/auth/logout` | Revoke a refresh token |
| GET | `/api/account/me` | Read the authenticated account |

Login response:

```json
{
  "accessToken": "<short-lived-jwt>",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "refreshToken": "<opaque-token>",
  "refreshTokenExpiresInSeconds": 2592000
}
```

Refresh and logout request:

```json
{
  "refreshToken": "<opaque-token>"
}
```

Use the access token only in the bearer header:

```bash
curl -i http://localhost:8080/api/account/me \
  -H "Authorization: Bearer <accessToken>"
```

## Token Lifecycle

1. Login creates a refresh-token family and stores only its SHA-256 hash.
2. Refresh validates the stored token, revokes it, and creates a replacement in the same family.
3. Reusing a revoked refresh token is treated as token theft: every active token in that family is revoked.
4. Logout revokes the submitted refresh token.

## Tests

The integration suite covers token issuance, access-token authorization, refresh rotation, replay detection, and logout revocation:

```bash
mvn test
```
