package com.javaforge.libraryapi.dto;

import com.javaforge.libraryapi.model.Book;

import java.time.Instant;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        int publicationYear,
        Long libraryId,
        String libraryName,
        Instant createdAt,
        Instant updatedAt
) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getPublicationYear(), book.getLibrary().getId(), book.getLibrary().getName(),
                book.getCreatedAt(), book.getUpdatedAt());
    }
}
