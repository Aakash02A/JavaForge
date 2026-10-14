# 40 - Email Service

A Spring Boot REST API that accepts plain-text email requests and submits them through Java Mail and an SMTP server. A console delivery mode is provided for credential-free local development and manual API testing.

## Features

- Send a plain-text email to one recipient.
- Validate recipient address, subject, and message body.
- Submit messages over SMTP using Spring Boot's Java Mail integration.
- Use console mode locally without an SMTP account or sending real email.
- Return clear validation and mail-provider failure responses.
- Keep SMTP credentials outside source control using environment variables.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Boot Starter Mail (Jakarta Mail)
- Spring Boot Validation
- Maven
- JUnit 5, Mockito, and MockMvc

## Run locally

```powershell
cd 40-Email-Service
mvn clean test
mvn spring-boot:run
```

Console mode is the default. It accepts the request and logs only the recipient and message lengths; it does not contact a mail server or log the message contents.

## Send a test request

In a second PowerShell window:

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

An accepted request returns HTTP `202 Accepted`:

```json
{
  "status": "ACCEPTED",
  "deliveryMode": "console",
  "recipient": "reader@example.com",
  "acceptedAt": "2026-10-14T10:00:00Z"
}
```

`ACCEPTED` means the configured sender accepted the operation. In console mode it does not mean an email was delivered.

## SMTP configuration

Set these environment variables before starting the application to use a real SMTP provider:

| Variable | Default | Purpose |
| --- | --- | --- |
| `MAIL_MODE` | `console` | Set to `smtp` to enable SMTP sending |
| `SMTP_HOST` | `localhost` | SMTP server hostname |
| `SMTP_PORT` | `587` | SMTP server port |
| `SMTP_USERNAME` | empty | SMTP account username |
| `SMTP_PASSWORD` | empty | SMTP account password or app password |
| `SMTP_AUTH` | `false` | Enable SMTP authentication when required |
| `SMTP_STARTTLS` | `true` | Enable STARTTLS |
| `SMTP_STARTTLS_REQUIRED` | `false` | Require STARTTLS or fail |
| `MAIL_FROM` | `no-reply@javaforge.local` | Sender address accepted by the provider |

Example PowerShell configuration (use provider-issued credentials):

```powershell
$env:MAIL_MODE = "smtp"
$env:SMTP_HOST = "smtp.example.com"
$env:SMTP_PORT = "587"
$env:SMTP_USERNAME = "your-smtp-username"
$env:SMTP_PASSWORD = "your-smtp-password"
$env:SMTP_AUTH = "true"
$env:SMTP_STARTTLS = "true"
$env:MAIL_FROM = "no-reply@example.com"
mvn spring-boot:run
```

Do not put real credentials in `application.properties`, README files, or committed `.env` files. The project `.gitignore` excludes common local override files; use a secret manager in deployed environments.

## API

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

The API accepts a single recipient and sends plain text. The recipient must be a valid email address, the subject is limited to 200 characters, and the body is limited to 10,000 characters.

| Status | Meaning |
| --- | --- |
| `202 Accepted` | The selected delivery adapter accepted the operation |
| `400 Bad Request` | The JSON payload or one of its fields is invalid |
| `502 Bad Gateway` | The SMTP adapter could not submit the message to the server |

Errors use a consistent JSON shape with a timestamp, HTTP status, error label, and message.

## Architecture

```text
HTTP client
    |
    v
EmailController -- validates request and returns HTTP response
    |
    v
EmailService -- coordinates the send and builds response metadata
    |
    v
EmailSender interface
    |                         |
    v                         v
SmtpEmailSender          ConsoleEmailSender
Spring JavaMailSender    safe local/manual test mode
```

The service depends on the `EmailSender` abstraction rather than a specific provider. `SmtpEmailSender` creates a UTF-8 plain-text MIME message and hands it to Spring's `JavaMailSender`. `ConsoleEmailSender` is the default local adapter and intentionally does not write message bodies or credentials to logs.

## Testing

```powershell
mvn test
```

The integration tests use MockMvc and a mocked sender, so they do not need network access, a mail account, or real recipients. They verify successful request handling, validation, and provider failure mapping.

## Production considerations

- An SMTP server accepting a message is not proof of inbox delivery; bounce handling and delivery events require provider support.
- Restrict who can call this endpoint, add rate limiting, and enforce recipient/domain policies before exposing it publicly.
- Consider asynchronous delivery and a durable queue for workloads where SMTP latency should not block API requests.
- Add idempotency keys, audit metadata, delivery metrics, and carefully redacted logs where the application requires them.
- Use provider-managed secrets and TLS settings appropriate to the deployment environment.
