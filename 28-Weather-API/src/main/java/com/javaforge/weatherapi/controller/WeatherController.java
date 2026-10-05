package com.javaforge.weatherapi.controller;

import com.javaforge.weatherapi.dto.WeatherReport;
import com.javaforge.weatherapi.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping
    public WeatherReport getCurrentWeather(
            @RequestParam
            @NotBlank(message = "location is required")
            @Size(max = 100, message = "location must be at most 100 characters")
            String location
    ) {
        return service.getCurrentWeather(location);
    }
}
