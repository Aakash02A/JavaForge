package com.javaforge.bookstore.repository;

import com.javaforge.bookstore.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);

    Optional<Book> findById(long id);

    List<Book> findAll();

    boolean deleteById(long id);
}
