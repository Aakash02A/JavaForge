package com.javaforge.urlshortener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShortUrlRepository repository;

    @BeforeEach
    void clearUrls() {
        repository.deleteAll();
    }

    @Test
    void shortenReturnsDeterministicUrlAndReusesExistingMapping() throws Exception {
        String payload = """
                {"url":"https://example.com/products?id=42"}
                """;

        String firstResponse = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").isString())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/products?id=42"))
                .andReturn().getResponse().getContentAsString();

        String secondResponse = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(secondResponse).get("shortUrl").asText())
                .isEqualTo(objectMapper.readTree(firstResponse).get("shortUrl").asText());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void lookupAndRedirectResolveTheOriginalUrlAndCountClicks() throws Exception {
        String response = create("https://example.com/docs/getting-started");
        String shortUrl = objectMapper.readTree(response).get("shortUrl").asText();
        String code = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);

        mockMvc.perform(get("/api/urls/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/docs/getting-started"))
                .andExpect(jsonPath("$.clickCount").value(0));

        mockMvc.perform(get("/" + code))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .isEqualTo("https://example.com/docs/getting-started"));

        assertThat(repository.findByShortCode(code).orElseThrow().getClickCount()).isEqualTo(1);
    }

    @Test
    void invalidAndUnknownUrlsReturnUsefulErrors() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"url":"not-a-url"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("URL must be an absolute HTTP or HTTPS URL"));

        mockMvc.perform(get("/api/urls/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    private String create(String url) throws Exception {
        return mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"url":"%s"}
                                """.formatted(url)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }
}
