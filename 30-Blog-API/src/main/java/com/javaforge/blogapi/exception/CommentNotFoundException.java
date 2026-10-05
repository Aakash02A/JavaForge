package com.javaforge.blogapi.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(long id, long postId) {
        super("Comment with id " + id + " was not found on post " + postId);
    }
}
