package com.javaforge.blogapi.dto;

import com.javaforge.blogapi.model.BlogComment;

import java.time.Instant;

public record CommentResponse(Long id, String commenterName, String content, Instant createdAt) {
    public static CommentResponse from(BlogComment comment) {
        return new CommentResponse(comment.getId(), comment.getCommenterName(),
                comment.getContent(), comment.getCreatedAt());
    }
}
