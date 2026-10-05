package com.javaforge.moviereviews.service;

import com.javaforge.moviereviews.dto.CreateMovieReviewRequest;
import com.javaforge.moviereviews.dto.ReviewPage;
import com.javaforge.moviereviews.dto.UpdateMovieReviewRequest;
import com.javaforge.moviereviews.exception.ReviewNotFoundException;
import com.javaforge.moviereviews.model.MovieReview;
import com.javaforge.moviereviews.repository.ReviewRepository;
import com.javaforge.moviereviews.repository.ReviewSortField;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class MovieReviewService {
    private final ReviewRepository repository;

    public MovieReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public MovieReview create(CreateMovieReviewRequest request) {
        Instant now = Instant.now();
        return repository.save(new MovieReview(null, request.movieTitle().trim(), request.reviewerName().trim(),
                request.rating(), request.comment().trim(), now, now));
    }

    public ReviewPage<MovieReview> findPage(int page, int size, String sortBy, String direction) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }

        ReviewSortField sortField = ReviewSortField.fromParameter(sortBy);
        Comparator<MovieReview> comparator = sortField.comparator();
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        } else if (!"asc".equalsIgnoreCase(direction)) {
            throw new IllegalArgumentException("direction must be either asc or desc");
        }

        List<MovieReview> all = repository.findAll().stream().sorted(comparator).toList();
        long totalElements = all.size();
        int totalPages = (int) ((totalElements + size - 1) / size);
        long offset = (long) page * size;
        List<MovieReview> content = offset >= totalElements
                ? List.of()
                : all.subList((int) offset, (int) Math.min(offset + size, totalElements));

        return new ReviewPage<>(content, page, size, totalElements, totalPages,
                page == 0, totalPages == 0 || page >= totalPages - 1);
    }

    public MovieReview findById(long id) {
        return repository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }

    public MovieReview update(long id, UpdateMovieReviewRequest request) {
        MovieReview existing = findById(id);
        return repository.save(new MovieReview(existing.id(), request.movieTitle().trim(),
                request.reviewerName().trim(), request.rating(), request.comment().trim(),
                existing.createdAt(), Instant.now()));
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new ReviewNotFoundException(id);
        }
    }
}
