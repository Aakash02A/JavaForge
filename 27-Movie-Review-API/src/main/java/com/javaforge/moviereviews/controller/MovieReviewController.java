package com.javaforge.moviereviews.controller;

import com.javaforge.moviereviews.dto.CreateMovieReviewRequest;
import com.javaforge.moviereviews.dto.ReviewPage;
import com.javaforge.moviereviews.dto.UpdateMovieReviewRequest;
import com.javaforge.moviereviews.model.MovieReview;
import com.javaforge.moviereviews.service.MovieReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/reviews")
public class MovieReviewController {
    private final MovieReviewService service;

    public MovieReviewController(MovieReviewService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MovieReview> create(@Valid @RequestBody CreateMovieReviewRequest request) {
        MovieReview created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ReviewPage<MovieReview> findPage(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be zero or greater") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be at least 1")
            @Max(value = 100, message = "size must be at most 100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return service.findPage(page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public MovieReview findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public MovieReview update(
            @PathVariable long id,
            @Valid @RequestBody UpdateMovieReviewRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
