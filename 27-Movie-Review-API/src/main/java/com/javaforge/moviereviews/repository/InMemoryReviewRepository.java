package com.javaforge.moviereviews.repository;

import com.javaforge.moviereviews.model.MovieReview;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryReviewRepository implements ReviewRepository {
    private final ConcurrentMap<Long, MovieReview> reviews = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public MovieReview save(MovieReview review) {
        long id = review.id() == null ? nextId.getAndIncrement() : review.id();
        MovieReview saved = new MovieReview(id, review.movieTitle(), review.reviewerName(), review.rating(),
                review.comment(), review.createdAt(), review.updatedAt());
        reviews.put(id, saved);
        return saved;
    }

    @Override
    public Optional<MovieReview> findById(long id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public List<MovieReview> findAll() {
        return reviews.values().stream()
                .sorted(Comparator.comparing(MovieReview::id))
                .toList();
    }

    @Override
    public boolean deleteById(long id) {
        return reviews.remove(id) != null;
    }
}
