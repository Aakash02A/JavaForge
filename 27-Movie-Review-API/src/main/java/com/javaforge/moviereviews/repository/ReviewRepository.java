package com.javaforge.moviereviews.repository;

import com.javaforge.moviereviews.model.MovieReview;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    MovieReview save(MovieReview review);

    Optional<MovieReview> findById(long id);

    List<MovieReview> findAll();

    boolean deleteById(long id);
}
