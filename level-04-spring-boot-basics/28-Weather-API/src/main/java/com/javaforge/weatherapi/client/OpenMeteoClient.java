package com.javaforge.weatherapi.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaforge.weatherapi.config.WeatherApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class OpenMeteoClient {
    private static final String CURRENT_FIELDS =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m";

    private final RestClient restClient;
    private final WeatherApiProperties properties;

    public OpenMeteoClient(RestClient weatherRestClient, WeatherApiProperties properties) {
        this.restClient = weatherRestClient;
        this.properties = properties;
    }

    public List<GeocodingResult> findLocations(String name) {
        URI uri = UriComponentsBuilder.fromUriString(properties.geocodingUrl())
                .queryParam("name", name)
                .queryParam("count", 1)
                .queryParam("language", "en")
                .queryParam("format", "json")
                .encode()
                .build()
                .toUri();
        GeocodingResponse response = restClient.get().uri(uri).retrieve().body(GeocodingResponse.class);
        return response == null || response.results() == null ? List.of() : response.results();
    }

    public ForecastResponse getCurrentWeather(double latitude, double longitude) {
        URI uri = UriComponentsBuilder.fromUriString(properties.forecastUrl())
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current", CURRENT_FIELDS)
                .queryParam("timezone", "auto")
                .encode()
                .build()
                .toUri();
        return restClient.get().uri(uri).retrieve().body(ForecastResponse.class);
    }

    public record GeocodingResponse(List<GeocodingResult> results) {
    }

    public record GeocodingResult(
            String name,
            double latitude,
            double longitude,
            String country,
            @JsonProperty("admin1") String region,
            @JsonProperty("timezone") String timezone
    ) {
    }

    public record ForecastResponse(
            String timezone,
            @JsonProperty("current") CurrentWeather current
    ) {
    }

    public record CurrentWeather(
            String time,
            @JsonProperty("temperature_2m") Double temperature,
            @JsonProperty("relative_humidity_2m") Integer relativeHumidity,
            @JsonProperty("apparent_temperature") Double apparentTemperature,
            @JsonProperty("is_day") Integer isDay,
            @JsonProperty("precipitation") Double precipitation,
            @JsonProperty("weather_code") Integer weatherCode,
            @JsonProperty("wind_speed_10m") Double windSpeed
    ) {
    }
}
