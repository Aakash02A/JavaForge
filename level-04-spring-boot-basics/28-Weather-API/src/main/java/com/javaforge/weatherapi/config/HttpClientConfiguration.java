package com.javaforge.weatherapi.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.net.http.HttpClient;

@Configuration
@EnableConfigurationProperties(WeatherApiProperties.class)
public class HttpClientConfiguration {
    @Bean
    RestClient weatherRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        return builder.requestFactory(requestFactory).build();
    }
}
