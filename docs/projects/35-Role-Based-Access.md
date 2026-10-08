# Project Idea

Build a **Role-Based Access Control API** that assigns users explicit roles and enforces those roles when clients access protected resources.

# Problem Statement

Authentication confirms a user's identity, but applications also need to decide what that identity is allowed to do. A regular account should not be able to access administrative actions simply because it has a valid login token. This project adds authorization rules to JWT authentication and demonstrates the distinction between authenticated requests and permitted requests.

# Objectives

- Define and persist account roles.
- Grant ordinary users only the default `USER` role during public registration.
- Restrict administrative routes to accounts with the `ADMIN` role.
- Return `401 Unauthorized` for requests without valid authentication.
- Return `403 Forbidden` when an authenticated user lacks the required role.
- Allow administrators to list accounts and change account roles.

# Features

- Register and authenticate accounts using JWT.
- Store account passwords as BCrypt hashes.
- Assign `USER` automatically at registration; never accept a role from public signup input.
- Provide a user dashboard accessible to `USER` and `ADMIN`.
- Provide administrator-only account listing and role-management routes.
- Bootstrap the initial administrator only from explicit environment configuration.
- Load the current role from storage for each authenticated request so role changes affect existing tokens.
- Return structured unauthorized and forbidden responses.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security route authorization
- Spring Data JPA and Hibernate
- H2 in-memory database
- JJWT
- BCrypt password encoder
- Bean Validation
- Maven

# Authorization Model

The application defines two roles:

- `USER`: assigned automatically to newly registered accounts; can use user-level resources.
- `ADMIN`: can access administrator resources, inspect account roles, and change user roles.

Spring Security represents these as `ROLE_USER` and `ROLE_ADMIN`. Route authorization checks the authenticated principal's granted authorities. Registration has no role field in its request DTO, preventing a caller from self-assigning administrator privileges.

# Authentication and Authorization Flow

1. Register or bootstrap an account. Public registration always creates a `USER`.
2. Log in with the account's email and password to receive a signed access token.
3. Send the token using the `Authorization: Bearer <accessToken>` header.
4. The JWT filter validates the signature and expiration and loads the current account and role from the database.
5. Spring Security permits or denies the request based on that role.
6. If authorized, an administrator can update another account's role. Because roles are reloaded for requests rather than trusted from stale token claims, the change applies to that account's next request.

# Setup and Run

From the project directory:

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8080`. H2 is in-memory, so no separate database server is required and account data is removed when the application stops.

To create the initial administrator, configure both `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` before starting the application. In PowerShell:

```powershell
$env:BOOTSTRAP_ADMIN_EMAIL = "admin@example.com"
$env:BOOTSTRAP_ADMIN_PASSWORD = "local-admin-password-123"
$env:JWT_SECRET = "<private-base64-encoded-key-of-at-least-32-bytes>"
mvn spring-boot:run
```

Both bootstrap settings must be supplied together. An existing account with a non-admin role is not silently elevated during startup; startup fails with an explicit configuration error instead. The built-in JWT signing key is for local learning only. Use a private signing key and HTTPS outside local development.

# API Endpoints

| Method | Path | Description | Access |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Register an account with the `USER` role | Public |
| `POST` | `/api/auth/login` | Authenticate and issue a JWT | Public |
| `GET` | `/api/user/dashboard` | Access the user dashboard | `USER` or `ADMIN` |
| `GET` | `/api/admin/dashboard` | Access the administrator dashboard | `ADMIN` |
| `GET` | `/api/admin/users` | List account summaries and roles | `ADMIN` |
| `PUT` | `/api/admin/users/{userId}/role` | Change an account's role | `ADMIN` |

Role update payload:

```json
{
  "role": "ADMIN"
}
```

Invalid request data returns `400 Bad Request`. A duplicate email returns `409 Conflict`. Missing or invalid authentication returns `401 Unauthorized`. A valid `USER` token calling an admin-only resource returns `403 Forbidden`. Updating the role of an unknown account returns `404 Not Found`.

# Example Requests

Register a regular user:

```bash
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"local-user-password-123"}'
```

Log in and use the user resource:

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"local-user-password-123"}'

curl -i http://localhost:8080/api/user/dashboard \
  -H "Authorization: Bearer <accessToken>"
```

Sign in as the configured administrator to list accounts and update roles:

```bash
curl -i http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <adminAccessToken>"

curl -i -X PUT http://localhost:8080/api/admin/users/1/role \
  -H "Authorization: Bearer <adminAccessToken>" \
  -H "Content-Type: application/json" \
  -d '{"role":"ADMIN"}'
```

Replace the token placeholders with access tokens from the login response.

# Security Scope and Limitations

- Public registration cannot request or choose an account role.
- Administrator provisioning requires explicit environment credentials.
- Do not expose bootstrap credentials or the JWT signing key in source control.
- Roles are queried from the database for authenticated requests so changes take effect without waiting for token expiration.
- The H2 database is ephemeral and intended for learning and local testing.
- This project demonstrates role authorization; OAuth login and refresh-token management are separate roadmap projects.

# Test

Run the integration tests and package the application:

```bash
mvn test
mvn package
```

Tests cover default `USER` assignment, user access, administrator restrictions, `401` versus `403` behavior, role changes taking effect with an existing token, and role-input validation.

# Project Details

Difficulty: ⭐⭐⭐⭐☆ (Advanced)

Estimated Time: 5–8 hours

Skills Gained:
- Role-based authorization
- Spring Security authorities and access rules
- Secure administrator bootstrap
- Authentication versus authorization semantics
- Role lifecycle and token interaction
