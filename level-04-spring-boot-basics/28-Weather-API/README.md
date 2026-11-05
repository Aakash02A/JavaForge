# 28 - Weather API

Build a **Weather API** that fetches real weather data from a public weather provider and exposes it through a clean Spring Boot endpoint.

# Problem Statement

Many applications need weather information but do not want to maintain a large weather dataset internally. Instead, they integrate with trusted providers through HTTP APIs. This project teaches how to call an external service, map the response to your own domain model, and handle failure cases cleanly.

# Objectives

- Connect to a third-party weather service.
- Accept a city or location query.
- Return current weather data in a clear API response.
- Handle invalid input and upstream failures.
- Build a backend that depends on external service reliability.

# Features

- Query current weather for a location.
- Return temperature, humidity, precipitation, and wind data.
- Map provider results into a defined API response class.
- Reject empty or overly long location values.
- Return structured errors for bad weather provider responses.

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- External API integration via HTTP
- Maven
- Open-Meteo provider

# Architecture and Flow

The weather service acts as a middle layer between the client and the upstream provider:

- Controller receives `GET /api/weather` requests.
- Service validates the location and calls the external weather API.
- Client model converts provider JSON into internal domain objects.
- Error handling protects the API from malformed or unavailable upstream data.

This makes the project a realistic example of integrating external systems without hardcoding credentials or generating fake data when the provider fails.

# Setup and Run

This project depends on internet access to Open-Meteo. Start it from the project directory:

```bash
mvn spring-boot:run
```

The app runs at:

```text
http://localhost:8080
```

# API Endpoint

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `GET` | `/api/weather?location={city}` | Get current weather for a location | `200 OK` |

The query requires a valid location string. Example response data may include:

- Temperature
- Feels-like temperature
- Humidity
- Precipitation
- Wind speed
- Weather condition
- Timezone
- Observation time

Error handling:

- Missing or invalid location returns `400 Bad Request`.
- Unknown location returns `404 Not Found`.
- External provider failure returns `502 Bad Gateway`.

# Example Requests

```bash
curl -i "http://localhost:8080/api/weather?location=London"
```

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 3–5 hours

Skills Gained:
- External API integration
- HTTP client usage in Java
- Error mapping and API resilience
- Working with third-party JSON responses
- Production-like API design

## Tests

Run `mvn test` to execute `WeatherControllerTest` and `WeatherServiceTest`. The controller tests use a mocked service and the service tests cover provider-response handling, so the suite does not need a live weather provider.
