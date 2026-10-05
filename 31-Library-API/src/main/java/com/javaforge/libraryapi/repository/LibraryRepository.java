package com.javaforge.libraryapi.repository;

import com.javaforge.libraryapi.model.Library;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, Long> {
    @Override
    @EntityGraph(attributePaths = "books")
    List<Library> findAll();

    @Override
    @EntityGraph(attributePaths = "books")
    Optional<Library> findById(Long id);
}
