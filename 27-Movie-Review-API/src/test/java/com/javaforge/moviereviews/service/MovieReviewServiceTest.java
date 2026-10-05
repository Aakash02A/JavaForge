package com.javaforge.moviereviews.service;

import com.javaforge.moviereviews.dto.CreateMovieReviewRequest;
import com.javaforge.moviereviews.dto.ReviewPage;
import com.javaforge.moviereviews.dto.UpdateMovieReviewRequest;
import com.javaforge.moviereviews.exception.ReviewNotFoundException;
import com.javaforge.moviereviews.model.MovieReview;
import com.javaforge.moviereviews.repository.InMemoryReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieReviewServiceTest {
    private MovieReviewService service;

    @BeforeEach
    void setUp() {
        service = new MovieReviewService(new InMemoryReviewRepository());
    }

    @Test
    void returnsCorrectPageMetadataAndStableSortedContent() {
        service.create(new CreateMovieReviewRequest("Arrival", "Ari", 4, "Thoughtful"));
        service.create(new CreateMovieReviewRequest("Dune", "Bea", 5, "Epic"));
        service.create(new CreateMovieReviewRequest("Arrival", "Cal", 3, "Good"));

        ReviewPage<MovieReview> page = service.findPage(1, 2, "rating", "desc");

        assertEquals(1, page.page());
        assertEquals(2, page.size());
        assertEquals(3, page.totalElements());
        assertEquals(2, page.totalPages());
        assertFalse(page.first());
        assertTrue(page.last());
        assertEquals(3, page.content().getFirst().rating());
    }

    @Test
    void returnsEmptyPageForPageBeyondEnd() {
        service.create(new CreateMovieReviewRequest("Arrival", "Ari", 4, "Thoughtful"));

        ReviewPage<MovieReview> page = service.findPage(4, 2, "createdAt", "asc");

        assertTrue(page.content().isEmpty());
        assertEquals(1, page.totalElements());
        assertEquals(1, page.totalPages());
        assertTrue(page.last());
    }

    @Test
    void rejectsInvalidPaginationAndSortOptions() {
        assertThrows(IllegalArgumentException.class, () -> service.findPage(-1, 10, "createdAt", "asc"));
        assertThrows(IllegalArgumentException.class, () -> service.findPage(0, 101, "createdAt", "asc"));
        assertThrows(IllegalArgumentException.class, () -> service.findPage(0, 10, "unknown", "asc"));
        assertThrows(IllegalArgumentException.class, () -> service.findPage(0, 10, "rating", "sideways"));
    }

    @Test
    void updatesAndDeletesReviewAndReportsMissingIds() {
        MovieReview created = service.create(new CreateMovieReviewRequest("Arrival", "Ari", 4, "Good"));
        MovieReview updated = service.update(created.id(),
                new UpdateMovieReviewRequest("Arrival", "Ari", 5, "Excellent"));

        assertEquals(5, updated.rating());
        assertEquals(created.createdAt(), updated.createdAt());
        service.delete(created.id());
        assertThrows(ReviewNotFoundException.class, () -> service.findById(created.id()));
    }
}
