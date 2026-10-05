package com.javaforge.libraryapi.repository;

import com.javaforge.libraryapi.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = "library")
    Optional<Book> findByIdAndLibraryId(Long id, Long libraryId);
}
