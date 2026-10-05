package com.javaforge.blogapi.dto;

import com.javaforge.blogapi.model.BlogPost;

import java.time.Instant;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorName,
        List<CommentResponse> comments,
        Instant createdAt,
        Instant updatedAt
) {
    public static PostResponse from(BlogPost post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(),
                post.getAuthor().getId(), post.getAuthor().getName(),
                post.getComments().stream().map(CommentResponse::from).toList(),
                post.getCreatedAt(), post.getUpdatedAt());
    }
}
