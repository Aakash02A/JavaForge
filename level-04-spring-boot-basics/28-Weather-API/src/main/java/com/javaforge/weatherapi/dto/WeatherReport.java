package com.javaforge.weatherapi.dto;

import java.time.Instant;

public record WeatherReport(
        String requestedLocation,
        String location,
        String region,
        String country,
        String timezone,
        double latitude,
        double longitude,
        double temperatureCelsius,
        double feelsLikeCelsius,
        int relativeHumidityPercent,
        double precipitationMillimeters,
        double windSpeedKmh,
        int weatherCode,
        String condition,
        boolean day,
        String observedAt,
        Instant fetchedAt
) {
}
