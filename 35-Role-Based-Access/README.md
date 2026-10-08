# Role-Based Access

A Java 21 / Spring Boot 3 API demonstrating role-based authorization (RBAC)
with Spring Security and JWT authentication. Public registration always creates
a `USER`; administrative access is provisioned explicitly and is never
accepted as a user-controlled registration field.

## Requirements

- JDK 21
- Maven 3.6+

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`. H2 stores users in memory; account
records are cleared when the process stops.

For local testing of administrator routes, configure both bootstrap variables
before starting the app. PowerShell example:

```powershell
$env:BOOTSTRAP_ADMIN_EMAIL = "admin@example.com"
$env:BOOTSTRAP_ADMIN_PASSWORD = "local-admin-password-123"
$env:JWT_SECRET = "<private-base64-encoded-key-of-at-least-32-bytes>"
mvn spring-boot:run
```

When starting with bootstrap credentials, the application creates that account
with the `ADMIN` role if it does not already exist. If only one bootstrap
setting is supplied, or the existing account is not an administrator, startup
fails rather than silently granting a role. Registration-created accounts
always receive `USER`.

The default signing key is for local learning only. Configure a private
Base64-encoded `JWT_SECRET` outside local development and use HTTPS in deployed
environments.

## Endpoints

| Method | Path | Description | Access |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Register a user account | Public |
| `POST` | `/api/auth/login` | Authenticate and issue a token | Public |
| `GET` | `/api/user/dashboard` | User-facing protected resource | USER or ADMIN |
| `GET` | `/api/admin/dashboard` | Administrator-only resource | ADMIN |
| `GET` | `/api/admin/users` | List accounts and roles | ADMIN |
| `PUT` | `/api/admin/users/{userId}/role` | Change a user's role | ADMIN |

Passwords are hashed with BCrypt. Registration passwords must be 12-72
characters. Emails are trimmed, normalized to lowercase, and unique. Invalid
requests return `400`; duplicate emails return `409`; missing or invalid
credentials return `401`; valid users without the required role receive `403`.

Roles are loaded from the database during bearer-token authentication. A role
change therefore takes effect on the user's next request, even when that user
already holds an unexpired token.

## Example

Register an ordinary user:

```powershell
$user = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/register `
  -ContentType "application/json" `
  -Body '{"email":"user@example.com","password":"local-user-password-123"}'
```

Log in and call the user route:

```powershell
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login `
  -ContentType "application/json" `
  -Body '{"email":"user@example.com","password":"local-user-password-123"}'

Invoke-RestMethod -Uri http://localhost:8080/api/user/dashboard `
  -Headers @{ Authorization = "Bearer $($login.accessToken)" }
```

Log in using the configured bootstrap administrator to call
`/api/admin/dashboard`, list accounts, or update roles. Do not permit public
clients to choose or elevate their own roles.

## Test and Build

```bash
mvn test
mvn package
```

Integration tests cover default role assignment, USER versus ADMIN access,
403 forbidden responses, admin role updates taking effect with an existing
token, input validation, and anonymous access rejection.
