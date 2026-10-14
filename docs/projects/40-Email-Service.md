# Project 40: Email Service

## Project Idea

Build a small backend service that accepts an email request over HTTP and submits it through an SMTP provider using Java Mail. Email is a common integration in account verification, password reset, order updates, and other application workflows. This project focuses on the boundary between an application API and an external mail transport, while remaining testable without real accounts.

## Problem Statement

Application code should not construct provider-specific messages inside HTTP controllers, expose mail credentials in source code, or make provider failures indistinguishable from successful requests. A reusable email service needs a clear API contract, input validation, a transport adapter, configuration that can be supplied safely at runtime, and explicit error behavior.

The service implements these concerns as a standalone Spring Boot application. SMTP is the real delivery integration; a console adapter supports local development and manual terminal checks when no SMTP account is configured.

## Objectives

- Accept a recipient, subject, and plain-text body via a REST endpoint.
- Reject missing, malformed, or oversized request fields.
- Use Spring Boot's Java Mail integration to submit mail through SMTP.
- Keep provider configuration and credentials outside committed source.
- Provide a local mode that does not contact an external mail provider.
- Map delivery failures to a clear HTTP error response.
- Verify request and error behavior without sending real messages in tests.

## Features

- `POST /api/emails` accepts one plain-text email request.
- Jakarta Bean Validation checks the recipient, subject, and body.
- SMTP settings are read from environment variables through Spring configuration.
- Console mode is the default and does not log message bodies or SMTP secrets.
- SMTP connection or message-construction failures return HTTP `502 Bad Gateway`.
- API errors use a consistent JSON structure.
- Tests replace the sender boundary with a mock and require no SMTP server.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Boot Starter Mail and Jakarta Mail
- Spring Boot Validation
- Maven
- JUnit 5, Mockito, and MockMvc

## Architecture

```text
EmailController
      |
      v
EmailService
      |
      v
EmailSender interface
      |                         |
      v                         v
SmtpEmailSender          ConsoleEmailSender
Spring JavaMailSender    local acceptance without transport
```

The controller owns the HTTP contract and validation boundary. The service coordinates the operation and produces response metadata. Both delivery implementations implement `EmailSender`, allowing the application to select an adapter through configuration instead of coupling business flow to SMTP classes.

The SMTP adapter builds a UTF-8 MIME message, sets the configured sender, recipient, subject, and plain-text body, then delegates transmission to Spring's `JavaMailSender`. The console adapter is intended for local development only. It records limited metadata and deliberately avoids logging the body, which may contain personal or confidential information.

## Email Request Flow

1. A client submits a JSON request to `POST /api/emails`.
2. Spring validates the email address and required text fields.
3. `EmailController` passes the valid request to `EmailService`.
4. The selected `EmailSender` adapter handles the delivery operation.
5. On acceptance, the service returns the selected mode, recipient, and acceptance timestamp.
6. If the SMTP adapter cannot submit the message, the exception handler returns `502` rather than reporting success.

The returned `ACCEPTED` status indicates acceptance by the configured adapter. In console mode, this is only a local simulation; it is not an email delivery receipt. Even in SMTP mode, provider acceptance does not guarantee delivery to the recipient's inbox.

## API Contract

### Send an email

`POST /api/emails`

Request:

```json
{
  "to": "reader@example.com",
  "subject": "Welcome",
  "body": "Thanks for joining."
}
```

Constraints:

- `to` is required, must be a valid email address, and is limited to 254 characters.
- `subject` is required and limited to 200 characters.
- `body` is required and limited to 10,000 characters.
- Only one recipient and plain-text content are supported.

Successful response (`202 Accepted`):

```json
{
  "status": "ACCEPTED",
  "deliveryMode": "console",
  "recipient": "reader@example.com",
  "acceptedAt": "2026-10-14T10:00:00Z"
}
```

Error responses use this structure:

```json
{
  "timestamp": "2026-10-14T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Recipient must be a valid email address"
}
```

| HTTP status | Meaning |
| --- | --- |
| `202 Accepted` | The selected sender accepted the operation |
| `400 Bad Request` | Request validation failed |
| `502 Bad Gateway` | SMTP submission or message creation failed |

## Configuration and Credential Safety

The default `MAIL_MODE=console` lets the application start and be exercised without an SMTP account. To use real SMTP, set `MAIL_MODE=smtp` and configure the server and credentials at runtime.

Supported environment variables include:

| Variable | Default | Purpose |
| --- | --- | --- |
| `MAIL_MODE` | `console` | Selects `console` or `smtp` adapter |
| `SMTP_HOST` | `localhost` | SMTP hostname |
| `SMTP_PORT` | `587` | SMTP port |
| `SMTP_USERNAME` | empty | SMTP account name |
| `SMTP_PASSWORD` | empty | SMTP account password or app password |
| `SMTP_AUTH` | `false` | Turns SMTP authentication on or off |
| `SMTP_STARTTLS` | `true` | Enables STARTTLS |
| `SMTP_STARTTLS_REQUIRED` | `false` | Makes STARTTLS mandatory |
| `MAIL_FROM` | `no-reply@javaforge.local` | Sender address configured with the mail provider |

Do not commit real credentials in properties files, documentation, or shell scripts. Use an environment-level secret store in deployed environments. The project-local `.gitignore` excludes common local environment and Spring override files.

## Setup and Manual Test

Run automated tests and start the local app:

```powershell
cd 40-Email-Service
mvn clean test
mvn spring-boot:run
```

The default console adapter can be manually exercised from another PowerShell window:

```powershell
$body = @{
  to = "reader@example.com"
  subject = "Hello from Java Forge"
  body = "This is a local email-service test."
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/emails `
  -ContentType "application/json" `
  -Body $body
```

The response should report `deliveryMode` as `console`. Application logs include recipient and lengths only, not message contents.

For actual SMTP integration, provide credentials through environment variables, set `MAIL_MODE=smtp`, and use an account or test mailbox controlled by the developer. Verify the provider's host, port, authentication policy, sender authorization, and TLS requirements before sending messages.

## Testing Strategy

The MockMvc integration suite exercises the API boundary while replacing `EmailSender` with a Mockito mock. The tests verify:

- a valid request returns `202` and passes the exact message fields to the adapter;
- an invalid recipient returns `400` without attempting delivery;
- a sender failure returns `502` with a stable API error response.

This avoids external-network dependencies and accidental emails during automated testing. The local console adapter provides an additional end-to-end manual check of application wiring and request handling.

## Limitations and Production Considerations

- The initial API is synchronous and supports only one plain-text recipient.
- SMTP acceptance is not equivalent to final delivery; production systems should handle bounces, provider callbacks, and delivery status where required.
- A public unauthenticated mail endpoint can be abused. Add authentication, rate limits, recipient controls, and abuse monitoring before deployment.
- A queue and retry policy can improve resilience and response latency for production workloads, but retries should account for duplicate-send risk.
- Avoid logging message contents and credentials; consider whether recipient addresses also need redaction under the system's privacy policy.
- Use a durable outbox or idempotency strategy if email submission must remain consistent with database transactions.

## Learning Outcomes

- Integrate Java Mail with a Spring Boot service.
- Configure external dependencies using environment-based properties.
- Separate API, business orchestration, and provider adapter responsibilities.
- Apply request validation and meaningful HTTP failure mapping.
- Test external integrations through a mockable boundary.
- Distinguish local acceptance, SMTP acceptance, and actual mailbox delivery.
