package com.javaforge.moviereviews.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(long id) {
        super("Review with id " + id + " was not found");
    }
}
