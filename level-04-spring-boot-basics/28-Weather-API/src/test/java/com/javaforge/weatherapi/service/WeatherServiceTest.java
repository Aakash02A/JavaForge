package com.javaforge.weatherapi.service;

import com.javaforge.weatherapi.client.OpenMeteoClient;
import com.javaforge.weatherapi.client.OpenMeteoClient.ForecastResponse;
import com.javaforge.weatherapi.client.OpenMeteoClient.GeocodingResponse;
import com.javaforge.weatherapi.client.OpenMeteoClient.GeocodingResult;
import com.javaforge.weatherapi.config.WeatherApiProperties;
import com.javaforge.weatherapi.exception.LocationNotFoundException;
import com.javaforge.weatherapi.exception.WeatherProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WeatherServiceTest {
    private static final String GEOCODING_URL = "https://geocoding.test/v1/search";
    private static final String FORECAST_URL = "https://forecast.test/v1/forecast";

    private MockRestServiceServer server;
    private WeatherService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        OpenMeteoClient client = new OpenMeteoClient(builder.build(),
                new WeatherApiProperties(GEOCODING_URL, FORECAST_URL));
        service = new WeatherService(client);
    }

    @Test
    void resolvesLocationAndMapsCurrentWeather() {
        server.expect(requestTo(URI.create(
                        GEOCODING_URL + "?name=London&count=1&language=en&format=json")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"results":[{"name":"London","latitude":51.5,"longitude":-0.1,
                        "country":"United Kingdom","admin1":"England","timezone":"Europe/London"}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));
        server.expect(requestTo(URI.create(
                        FORECAST_URL + "?latitude=51.5&longitude=-0.1&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m&timezone=auto")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"timezone":"Europe/London","current":{"time":"2026-10-05T12:00",
                        "temperature_2m":18.5,"relative_humidity_2m":62,"apparent_temperature":18.1,
                        "is_day":1,"precipitation":0.0,"weather_code":2,"wind_speed_10m":11.3}}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        var report = service.getCurrentWeather(" London ");

        assertEquals("London", report.requestedLocation());
        assertEquals("England", report.region());
        assertEquals(18.5, report.temperatureCelsius());
        assertEquals("Partly cloudy", report.condition());
        assertTrue(report.day());
        assertEquals("2026-10-05T12:00", report.observedAt());
        server.verify();
    }

    @Test
    void reportsUnknownLocationWithoutCallingForecast() {
        server.expect(requestTo(GEOCODING_URL + "?name=Atlantis&count=1&language=en&format=json"))
                .andRespond(withSuccess("{}", org.springframework.http.MediaType.APPLICATION_JSON));

        assertThrows(LocationNotFoundException.class, () -> service.getCurrentWeather("Atlantis"));
        server.verify();
    }

    @Test
    void surfacesIncompleteProviderResponseAsProviderFailure() {
        server.expect(requestTo(GEOCODING_URL + "?name=London&count=1&language=en&format=json"))
                .andRespond(withSuccess("""
                        {"results":[{"name":"London","latitude":51.5,"longitude":-0.1,
                        "country":"United Kingdom","timezone":"Europe/London"}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));
        server.expect(requestTo(URI.create(
                        FORECAST_URL + "?latitude=51.5&longitude=-0.1&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m&timezone=auto")))
                .andRespond(withSuccess("{\"timezone\":\"Europe/London\"}",
                        org.springframework.http.MediaType.APPLICATION_JSON));

        assertThrows(WeatherProviderException.class, () -> service.getCurrentWeather("London"));
        server.verify();
    }
}
