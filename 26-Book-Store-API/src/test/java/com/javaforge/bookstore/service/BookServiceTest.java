package com.javaforge.bookstore.service;

import com.javaforge.bookstore.dto.CreateBookRequest;
import com.javaforge.bookstore.dto.UpdateBookRequest;
import com.javaforge.bookstore.exception.BookNotFoundException;
import com.javaforge.bookstore.exception.DuplicateIsbnException;
import com.javaforge.bookstore.model.Book;
import com.javaforge.bookstore.repository.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookServiceTest {
    private BookService service;

    @BeforeEach
    void setUp() {
        service = new BookService(new InMemoryBookRepository());
    }

    @Test
    void createsAndListsBooksWithNormalizedValues() {
        Book created = service.create(new CreateBookRequest(
                "  Clean Code  ", "  Robert Martin  ", " 978-0-13-235088-4 ", new BigDecimal("39.99"), 8));

        assertEquals("Clean Code", created.title());
        assertEquals("Robert Martin", created.author());
        assertEquals("9780132350884", created.isbn());
        assertEquals(new BigDecimal("39.99"), created.price());
        assertEquals(8, created.stock());
        assertEquals(created.createdAt(), created.updatedAt());
        assertEquals(List.of(created), service.findAll());
    }

    @Test
    void updatesBookAndPreservesCreationTime() {
        Book created = service.create(new CreateBookRequest(
                "Clean Code", "Robert Martin", "978-0-13-235088-4", new BigDecimal("39.99"), 8));
        Book updated = service.update(created.id(), new UpdateBookRequest(
                "Clean Coder", "Robert Martin", "9780132350884", new BigDecimal("44.50"), 5));

        assertEquals(created.id(), updated.id());
        assertEquals("Clean Coder", updated.title());
        assertEquals(new BigDecimal("44.50"), updated.price());
        assertEquals(5, updated.stock());
        assertEquals(created.createdAt(), updated.createdAt());
    }

    @Test
    void rejectsDuplicateIsbnsIgnoringHyphensAndCase() {
        service.create(new CreateBookRequest(
                "Clean Code", "Robert Martin", "978-0-13-235088-4", new BigDecimal("39.99"), 8));

        assertThrows(DuplicateIsbnException.class, () -> service.create(new CreateBookRequest(
                "Another Book", "Another Author", "9780132350884", new BigDecimal("20.00"), 2)));
    }

    @Test
    void deletesBookAndReportsMissingIds() {
        Book created = service.create(new CreateBookRequest(
                "Clean Code", "Robert Martin", "978-0-13-235088-4", new BigDecimal("39.99"), 8));
        service.delete(created.id());

        assertTrue(service.findAll().isEmpty());
        assertThrows(BookNotFoundException.class, () -> service.findById(created.id()));
        assertThrows(BookNotFoundException.class, () -> service.delete(created.id()));
    }
}
