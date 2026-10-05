# Weather API

A Java 21 / Spring Boot 3 API that retrieves current weather using Open-Meteo's
geocoding and forecast APIs. It demonstrates an external API integration without
requiring an API key. The provider URLs are configurable in
`src/main/resources/application.properties`.

## Requirements

- JDK 21
- Maven 3.6+
- Internet access to Open-Meteo

## Run

From this directory:

```bash
mvn spring-boot:run
```

The API listens at `http://localhost:8080`.

## Endpoint

`GET /api/weather?location={city}` resolves the location and returns current
conditions, including temperature, feels-like temperature, humidity,
precipitation, wind speed, weather condition, timezone, and observation time.
Location is required and may be at most 100 characters.

Example:

```bash
curl -i "http://localhost:8080/api/weather?location=London"
```

An unknown location returns `404`; invalid input returns `400`; provider/network
failures or incomplete upstream responses return `502 Bad Gateway` with a
structured error body. The API does not return fabricated weather if the
provider is unavailable.

## Test

```bash
mvn test
mvn package
```

Automated tests mock the upstream HTTP responses and cover mapping, unknown
locations, invalid input, and provider failures. Run the app with internet
access to check live weather data.
