package com.javaforge.bookstore.controller;

import com.javaforge.bookstore.dto.CreateBookRequest;
import com.javaforge.bookstore.dto.UpdateBookRequest;
import com.javaforge.bookstore.model.Book;
import com.javaforge.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody CreateBookRequest request) {
        Book created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<Book> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Book findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable long id, @Valid @RequestBody UpdateBookRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
