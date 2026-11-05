package com.javaforge.weatherapi.controller;

import com.javaforge.weatherapi.dto.WeatherReport;
import com.javaforge.weatherapi.exception.ApiExceptionHandler;
import com.javaforge.weatherapi.exception.LocationNotFoundException;
import com.javaforge.weatherapi.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
@Import(ApiExceptionHandler.class)
class WeatherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService service;

    @Test
    void returnsWeatherForRequestedLocation() throws Exception {
        when(service.getCurrentWeather("London")).thenReturn(new WeatherReport(
                "London", "London", "England", "United Kingdom", "Europe/London",
                51.5, -0.1, 18.5, 18.1, 62, 0, 11.3, 2, "Partly cloudy", true,
                "2026-10-05T12:00", Instant.parse("2026-10-05T12:01:00Z")));

        mockMvc.perform(get("/api/weather").param("location", "London"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("London"))
                .andExpect(jsonPath("$.temperatureCelsius").value(18.5))
                .andExpect(jsonPath("$.condition").value("Partly cloudy"));
    }

    @Test
    void rejectsBlankLocationParameter() throws Exception {
        mockMvc.perform(get("/api/weather").param("location", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.location").value("location is required"));
    }

    @Test
    void mapsUnknownLocationToNotFound() throws Exception {
        when(service.getCurrentWeather("Atlantis"))
                .thenThrow(new LocationNotFoundException("Atlantis"));

        mockMvc.perform(get("/api/weather").param("location", "Atlantis"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
