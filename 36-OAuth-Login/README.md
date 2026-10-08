# OAuth Login

A Java 21 / Spring Boot 3 application demonstrating OAuth2 Login with Spring
Security. Users authenticate with an external provider such as Google, and the
application creates or updates a local profile using the provider subject,
email, and display name.

## Requirements

- JDK 21
- Maven 3.6+
- An OAuth2 provider application for live login, such as a Google OAuth client

## Configure and Run

Create an OAuth client in the provider console and register this redirect URI:

```text
http://localhost:8080/login/oauth2/code/google
```

Set credentials outside source control. PowerShell example:

```powershell
$env:GOOGLE_CLIENT_ID = "<provider-client-id>"
$env:GOOGLE_CLIENT_SECRET = "<provider-client-secret>"
mvn spring-boot:run
```

Open this URL in a browser:

```text
http://localhost:8080/oauth2/authorization/google
```

The provider authenticates the user and redirects back to the application.
The local H2 database stores the resulting OAuth profile and the browser
session remains authenticated. Account data is cleared when the app stops.

The default credentials in `application.properties` are placeholders for
automated startup only and cannot perform a real provider login. Never commit
real client secrets.

## Endpoint

| Method | Path | Description | Access |
| --- | --- | --- | --- |
| `GET` | `/oauth2/authorization/google` | Start provider login | Public |
| `GET` | `/login/oauth2/code/google` | Provider callback | Provider callback |
| `GET` | `/api/profile` | Get the local authenticated profile | OAuth session |
| `POST` | `/logout` | End the local session | Authenticated |

The login service identifies accounts by the provider registration ID and
provider subject rather than trusting email as the primary identity. Email and
display name are synchronized when the same provider account logs in again.

## Test and Build

```bash
mvn test
mvn package
```

The integration tests use Spring Security's OAuth2 test support, so they do not
call Google or require credentials. They verify anonymous protection, profile
creation after an OAuth login, email normalization, repeat-login synchronization,
duplicate prevention, and missing local-profile handling.
