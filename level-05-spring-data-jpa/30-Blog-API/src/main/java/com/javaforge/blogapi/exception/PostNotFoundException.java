package com.javaforge.blogapi.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(long id) {
        super("Blog post with id " + id + " was not found");
    }
}
