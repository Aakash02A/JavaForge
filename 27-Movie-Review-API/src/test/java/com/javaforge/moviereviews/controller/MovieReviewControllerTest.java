package com.javaforge.moviereviews.controller;

import com.javaforge.moviereviews.exception.ApiExceptionHandler;
import com.javaforge.moviereviews.exception.ReviewNotFoundException;
import com.javaforge.moviereviews.model.MovieReview;
import com.javaforge.moviereviews.service.MovieReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieReviewController.class)
@Import(ApiExceptionHandler.class)
class MovieReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieReviewService service;

    @Test
    void returnsPaginatedReviewResponse() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        MovieReview review = new MovieReview(1L, "Arrival", "Ari", 5, "Excellent", timestamp, timestamp);
        when(service.findPage(0, 1, "rating", "desc"))
                .thenReturn(new com.javaforge.moviereviews.dto.ReviewPage<>(
                        List.of(review), 0, 1, 3, 3, true, false));

        mockMvc.perform(get("/api/reviews")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sortBy", "rating")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].movieTitle").value("Arrival"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void rejectsInvalidPageSize() throws Exception {
        mockMvc.perform(get("/api/reviews").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createsReviewAndReturnsLocationHeader() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        when(service.create(any())).thenReturn(
                new MovieReview(7L, "Arrival", "Ari", 5, "Excellent", timestamp, timestamp));

        mockMvc.perform(post("/api/reviews")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"movieTitle":"Arrival","reviewerName":"Ari","rating":5,"comment":"Excellent"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/reviews/7"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void returnsStructuredNotFoundResponse() throws Exception {
        when(service.findById(eq(99L))).thenThrow(new ReviewNotFoundException(99));

        mockMvc.perform(get("/api/reviews/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/reviews/99"));
    }
}
