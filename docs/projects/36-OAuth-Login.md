# Project Idea

Build an **OAuth2 Login API** that delegates identity verification to an external provider and maintains a local application profile for each signed-in user.

# Problem Statement

Applications should not need to collect or store passwords when a trusted identity provider can authenticate users. OAuth2 Login lets a user sign in through a provider such as Google while the application receives verified identity information through a secure authorization-code flow. The application still needs to map that external identity to a local account and protect its own resources.

# Objectives

- Configure Spring Security OAuth2 Login.
- Start a provider authorization flow and handle the callback.
- Load provider user information after consent.
- Persist a local profile linked to the provider and stable provider subject.
- Synchronize profile information on repeat login.
- Protect an application endpoint with the OAuth session.
- Keep provider credentials outside source control.

# Features

- Google OAuth2 provider configuration.
- Browser-based authorization-code login.
- Local H2 profile persistence.
- Provider-subject identity mapping.
- Email normalization and display-name synchronization.
- Protected `/api/profile` endpoint.
- Local logout support.
- Deterministic tests using Spring Security OAuth2 mock authentication.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security OAuth2 Client
- Spring Data JPA and Hibernate
- H2 in-memory database
- Bean validation through Spring Security's provider flow
- Maven

# OAuth2 Login Flow

1. The client opens `/oauth2/authorization/google`.
2. Spring Security redirects the browser to the configured provider.
3. The provider authenticates the user, obtains consent, and redirects to `/login/oauth2/code/google`.
4. Spring Security exchanges the authorization code for provider tokens.
5. The OAuth2 user service loads the provider profile.
6. The application identifies the account by provider registration ID and provider subject.
7. A new local profile is created, or an existing profile's email and display name are updated.
8. Spring Security creates an authenticated browser session.
9. The client calls `/api/profile` using that authenticated session.

The provider subject is the stable identity key. Email is stored for application display and communication, but it is not used as the primary external identity key.

# Architecture

- **Security configuration:** enables OAuth2 Login, permits authorization endpoints, and requires authentication for application routes.
- **OAuth account service:** delegates provider user loading, extracts safe attributes, and upserts the local profile.
- **JPA entity:** stores provider, provider subject, email, display name, creation time, and last login time.
- **Repository:** finds profiles by provider and provider subject.
- **Profile controller:** returns the local profile for the authenticated OAuth principal.
- **DTO:** prevents persistence implementation details from being returned directly.

# Setup and Run

Create a client application with the provider and add this redirect URI:

```text
http://localhost:8080/login/oauth2/code/google
```

Set credentials in the environment:

```powershell
$env:GOOGLE_CLIENT_ID = "<provider-client-id>"
$env:GOOGLE_CLIENT_SECRET = "<provider-client-secret>"
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/oauth2/authorization/google
```

The checked-in client values are placeholders and cannot authenticate against Google. Real credentials must never be committed. Use HTTPS and provider-approved redirect URIs outside local development.

# API and OAuth Endpoints

| Method | Path | Description | Access |
| --- | --- | --- | --- |
| `GET` | `/oauth2/authorization/google` | Begin Google login | Public |
| `GET` | `/login/oauth2/code/google` | Receive the provider callback | Provider callback |
| `GET` | `/api/profile` | Return the persisted local profile | Authenticated OAuth session |
| `POST` | `/logout` | End the application session | Authenticated |

An OAuth profile response contains:

```json
{
  "id": 1,
  "provider": "google",
  "email": "asha@example.com",
  "displayName": "Asha Rao",
  "createdAt": "2026-10-08T00:00:00Z",
  "lastLoginAt": "2026-10-08T00:00:00Z"
}
```

When the provider account logs in again, the existing profile is updated rather than duplicated. The application does not expose provider access or refresh tokens through the profile endpoint.

# Security Scope and Limitations

- OAuth client credentials must be supplied through environment variables or a secret manager.
- Provider scopes are limited to `openid`, `profile`, and `email`.
- H2 is in-memory for learning; production systems should use a persistent database.
- The application uses a server-side session after login. JWT access tokens and refresh-token rotation are separate roadmap topics.
- Provider account linking, unlinking, account recovery, and multiple-provider merging are outside this milestone.

# Test

Run the automated tests and package the application:

```bash
mvn test
mvn package
```

The tests mock the OAuth2 principal rather than calling a real provider. They verify anonymous protection, first-login profile creation, normalized emails, repeat-login updates without duplicates, and missing-profile behavior.

# Project Details

Difficulty: ⭐⭐⭐⭐☆ (Advanced)

Estimated Time: 5–8 hours

Skills Gained:
- OAuth2 authorization-code flow
- Spring Security OAuth2 Client
- External identity to local profile mapping
- Secure provider configuration
- Session-based authenticated API access
