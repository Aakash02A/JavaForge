package com.javaforge.bookstore.service;

import com.javaforge.bookstore.dto.CreateBookRequest;
import com.javaforge.bookstore.dto.UpdateBookRequest;
import com.javaforge.bookstore.exception.BookNotFoundException;
import com.javaforge.bookstore.model.Book;
import com.javaforge.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Book create(CreateBookRequest request) {
        Instant now = Instant.now();
        return repository.save(new Book(null, request.title().trim(), request.author().trim(),
                request.isbn().trim(), request.price(), request.stock(), now, now));
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book findById(long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book update(long id, UpdateBookRequest request) {
        Book existing = findById(id);
        return repository.save(new Book(existing.id(), request.title().trim(), request.author().trim(),
                request.isbn().trim(), request.price(), request.stock(), existing.createdAt(), Instant.now()));
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new BookNotFoundException(id);
        }
    }
}
