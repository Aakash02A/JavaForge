package com.javaforge.blogapi.dto;

import com.javaforge.blogapi.model.Author;

import java.time.Instant;

public record AuthorResponse(
        Long id,
        String name,
        String email,
        String bio,
        long postCount,
        Instant createdAt
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.getId(), author.getName(), author.getEmail(), author.getBio(),
                author.getPosts().size(), author.getCreatedAt());
    }
}
