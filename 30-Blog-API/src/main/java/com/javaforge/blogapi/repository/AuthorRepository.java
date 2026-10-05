package com.javaforge.blogapi.repository;

import com.javaforge.blogapi.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
