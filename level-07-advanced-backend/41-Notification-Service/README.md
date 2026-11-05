# 41 - Notification Service

A Spring Boot REST API that accepts notifications and processes them on a bounded background thread pool. The included console sender is a local demonstration adapter; it does not contact email, SMS, or push providers.

## Features

- Accept notifications for email, SMS, and push channels.
- Validate notification channel, recipient, and message.
- Return HTTP `202 Accepted` with a notification ID and status URL.
- Process accepted work asynchronously using a bounded executor.
- Look up in-memory status as notifications move from `QUEUED` to `PROCESSING`, then `COMPLETED` or `FAILED`.
- Return a clear error when the work queue is full.
- Keep notification message contents and recipient details out of application logs.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Boot Validation
- Maven
- JUnit 5, Mockito, MockMvc, and Awaitility

## Run locally

```powershell
cd 41-Notification-Service
mvn clean test
mvn spring-boot:run
```

The service listens on `http://localhost:8080`. No database, provider credentials, or external services are required.

## API

### Submit a notification

`POST /api/notifications`

Request:

```json
{
  "recipient": "reader@example.com",
  "channel": "EMAIL",
  "message": "Welcome to Java Forge."
}
```

`channel` must be `EMAIL`, `SMS`, or `PUSH`. The recipient must not be blank and is limited to 320 characters. The message must not be blank and is limited to 10,000 characters.

PowerShell example:

```powershell
$body = @{
  recipient = "reader@example.com"
  channel = "EMAIL"
  message = "Welcome to Java Forge."
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/notifications `
  -ContentType "application/json" `
  -Body $body
```

Example response:

```json
{
  "id": "8e584f35-4319-4a03-a34a-c62695acd9a3",
  "channel": "EMAIL",
  "status": "QUEUED",
  "submittedAt": "2026-10-14T10:00:00Z",
  "updatedAt": "2026-10-14T10:00:00Z",
  "statusUrl": "/api/notifications/8e584f35-4319-4a03-a34a-c62695acd9a3",
  "failureReason": null
}
```

`QUEUED` means the task was accepted for background processing, not delivered. In console mode, successful processing only demonstrates the asynchronous workflow.

### Check status

`GET /api/notifications/{id}`

Poll the `statusUrl` returned when submitting a notification. Statuses are `QUEUED`, `PROCESSING`, `COMPLETED`, and `FAILED`. Failed notifications include a generic `failureReason`; provider exception details are kept out of the API response.

| Status | Meaning |
| --- | --- |
| `202 Accepted` | Notification was queued for asynchronous processing |
| `200 OK` | Status lookup succeeded |
| `400 Bad Request` | Request fields are invalid or the channel is unsupported |
| `404 Not Found` | No in-memory notification exists for the ID |
| `503 Service Unavailable` | The bounded work queue rejected the notification |

Errors return JSON containing a timestamp, HTTP status, error label, and message.

## Architecture

```text
HTTP client
    |
    v
NotificationController -- validates and accepts requests / returns status
    |
    v
NotificationService -- tracks status and submits work to a bounded executor
    |
    v
NotificationSender
    |
    v
ConsoleNotificationSender -- demo adapter; logs ID and channel only
```

The executor runs two core threads, allows up to four threads, and has a queue capacity of 100. Status records live in process memory: they are lost on restart and are not shared across application instances. Replace the console sender with a provider integration and use durable storage/message infrastructure before relying on this service in production.

## Tests

```powershell
mvn test
```

Integration tests use MockMvc and a mocked sender. They verify asynchronous processing and status updates, validation, failure reporting, and unknown-ID handling without contacting external providers.
