package com.javaforge.bookstore.controller;

import com.javaforge.bookstore.exception.ApiExceptionHandler;
import com.javaforge.bookstore.exception.BookNotFoundException;
import com.javaforge.bookstore.model.Book;
import com.javaforge.bookstore.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(ApiExceptionHandler.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService service;

    @Test
    void createsBookAndReturnsLocationHeader() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        when(service.create(any())).thenReturn(
                new Book(7L, "Clean Code", "Robert Martin", "9780132350884",
                        new BigDecimal("39.99"), 8, timestamp, timestamp));

        mockMvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","author":"Robert Martin","isbn":"978-0-13-235088-4","price":39.99,"stock":8}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/books/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.price").value(39.99));
    }

    @Test
    void rejectsInvalidBookFieldsWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":" ","author":"","isbn":"","price":0,"stock":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.title").value("Title is required"))
                .andExpect(jsonPath("$.validationErrors.author").value("Author is required"))
                .andExpect(jsonPath("$.validationErrors.isbn").value("ISBN is required"))
                .andExpect(jsonPath("$.validationErrors.price").value("Price must be greater than zero"))
                .andExpect(jsonPath("$.validationErrors.stock").value("Stock cannot be negative"));
    }

    @Test
    void returnsStructuredNotFoundResponse() throws Exception {
        when(service.findById(eq(99L))).thenThrow(new BookNotFoundException(99));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Book with id 99 was not found"))
                .andExpect(jsonPath("$.path").value("/api/books/99"));
    }
}
