package com.javaforge.bookstore.repository;

import com.javaforge.bookstore.exception.DuplicateIsbnException;
import com.javaforge.bookstore.model.Book;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBookRepository implements BookRepository {
    private final ConcurrentMap<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public synchronized Book save(Book book) {
        String normalizedIsbn = normalizeIsbn(book.isbn());
        boolean isbnAlreadyUsed = books.values().stream()
                .anyMatch(existing -> !existing.id().equals(book.id())
                        && normalizeIsbn(existing.isbn()).equals(normalizedIsbn));
        if (isbnAlreadyUsed) {
            throw new DuplicateIsbnException(book.isbn());
        }

        long id = book.id() == null ? nextId.getAndIncrement() : book.id();
        Book saved = new Book(id, book.title(), book.author(), normalizedIsbn, book.price(),
                book.stock(), book.createdAt(), book.updatedAt());
        books.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public List<Book> findAll() {
        return books.values().stream()
                .sorted(Comparator.comparing(Book::id))
                .toList();
    }

    @Override
    public boolean deleteById(long id) {
        return books.remove(id) != null;
    }

    private String normalizeIsbn(String isbn) {
        return isbn.trim().replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
    }
}
