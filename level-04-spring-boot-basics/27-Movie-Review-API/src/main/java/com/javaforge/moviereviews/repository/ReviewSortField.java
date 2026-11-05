package com.javaforge.moviereviews.repository;

import com.javaforge.moviereviews.model.MovieReview;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public enum ReviewSortField {
    CREATED_AT("createdAt", Comparator.comparing(MovieReview::createdAt)),
    MOVIE_TITLE("movieTitle", Comparator.comparing(review -> review.movieTitle().toLowerCase(Locale.ROOT))),
    REVIEWER_NAME("reviewerName", Comparator.comparing(review -> review.reviewerName().toLowerCase(Locale.ROOT))),
    RATING("rating", Comparator.comparingInt(MovieReview::rating));

    private final String parameterName;
    private final Comparator<MovieReview> comparator;

    ReviewSortField(String parameterName, Comparator<MovieReview> comparator) {
        this.parameterName = parameterName;
        this.comparator = comparator;
    }

    public Comparator<MovieReview> comparator() {
        return comparator.thenComparing(MovieReview::id);
    }

    public static ReviewSortField fromParameter(String parameter) {
        return Arrays.stream(values())
                .filter(field -> field.parameterName.equalsIgnoreCase(parameter))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "sortBy must be one of: createdAt, movieTitle, reviewerName, rating"));
    }
}
