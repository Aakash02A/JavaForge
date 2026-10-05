package com.javaforge.libraryapi.dto;

import com.javaforge.libraryapi.model.Library;

import java.time.Instant;
import java.util.List;

public record LibraryResponse(
        Long id,
        String name,
        String address,
        List<BookResponse> books,
        Instant createdAt,
        Instant updatedAt
) {
    public static LibraryResponse from(Library library) {
        return new LibraryResponse(library.getId(), library.getName(), library.getAddress(),
                library.getBooks().stream().map(BookResponse::from).toList(),
                library.getCreatedAt(), library.getUpdatedAt());
    }
}
