package com.javaforge.weatherapi.service;

import com.javaforge.weatherapi.client.OpenMeteoClient;
import com.javaforge.weatherapi.client.OpenMeteoClient.CurrentWeather;
import com.javaforge.weatherapi.client.OpenMeteoClient.ForecastResponse;
import com.javaforge.weatherapi.client.OpenMeteoClient.GeocodingResult;
import com.javaforge.weatherapi.dto.WeatherReport;
import com.javaforge.weatherapi.exception.LocationNotFoundException;
import com.javaforge.weatherapi.exception.WeatherProviderException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private static final Map<Integer, String> CONDITIONS = Map.ofEntries(
            Map.entry(0, "Clear sky"),
            Map.entry(1, "Mainly clear"),
            Map.entry(2, "Partly cloudy"),
            Map.entry(3, "Overcast"),
            Map.entry(45, "Fog"),
            Map.entry(48, "Depositing rime fog"),
            Map.entry(51, "Light drizzle"),
            Map.entry(53, "Moderate drizzle"),
            Map.entry(55, "Dense drizzle"),
            Map.entry(56, "Light freezing drizzle"),
            Map.entry(57, "Dense freezing drizzle"),
            Map.entry(61, "Slight rain"),
            Map.entry(63, "Moderate rain"),
            Map.entry(65, "Heavy rain"),
            Map.entry(66, "Light freezing rain"),
            Map.entry(67, "Heavy freezing rain"),
            Map.entry(71, "Slight snow"),
            Map.entry(73, "Moderate snow"),
            Map.entry(75, "Heavy snow"),
            Map.entry(77, "Snow grains"),
            Map.entry(80, "Slight rain showers"),
            Map.entry(81, "Moderate rain showers"),
            Map.entry(82, "Violent rain showers"),
            Map.entry(85, "Slight snow showers"),
            Map.entry(86, "Heavy snow showers"),
            Map.entry(95, "Thunderstorm"),
            Map.entry(96, "Thunderstorm with slight hail"),
            Map.entry(99, "Thunderstorm with heavy hail")
    );

    private final OpenMeteoClient client;

    public WeatherService(OpenMeteoClient client) {
        this.client = client;
    }

    public WeatherReport getCurrentWeather(String requestedLocation) {
        try {
            String normalizedLocation = requestedLocation.trim();
            List<GeocodingResult> locations = client.findLocations(normalizedLocation);
            if (locations.isEmpty()) {
                throw new LocationNotFoundException(normalizedLocation);
            }
            GeocodingResult location = locations.getFirst();
            ForecastResponse forecast = client.getCurrentWeather(location.latitude(), location.longitude());
            if (forecast == null || forecast.current() == null) {
                throw new WeatherProviderException("Weather provider returned no current conditions");
            }
            CurrentWeather current = forecast.current();
            if (current.temperature() == null || current.apparentTemperature() == null
                    || current.relativeHumidity() == null || current.precipitation() == null
                    || current.windSpeed() == null || current.weatherCode() == null
                    || current.isDay() == null || current.time() == null) {
                throw new WeatherProviderException("Weather provider returned incomplete current conditions");
            }
            return new WeatherReport(
                    normalizedLocation,
                    location.name(),
                    location.region(),
                    location.country(),
                    forecast.timezone(),
                    location.latitude(),
                    location.longitude(),
                    current.temperature(),
                    current.apparentTemperature(),
                    current.relativeHumidity(),
                    current.precipitation(),
                    current.windSpeed(),
                    current.weatherCode(),
                    CONDITIONS.getOrDefault(current.weatherCode(), "Unknown"),
                    current.isDay() == 1,
                    current.time(),
                    Instant.now()
            );
        } catch (LocationNotFoundException | WeatherProviderException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new WeatherProviderException("Unable to retrieve weather from the provider", exception);
        }
    }
}
