package com.javaforge.libraryapi.controller;

import com.javaforge.libraryapi.dto.BookResponse;
import com.javaforge.libraryapi.dto.CreateBookRequest;
import com.javaforge.libraryapi.dto.CreateLibraryRequest;
import com.javaforge.libraryapi.dto.LibraryResponse;
import com.javaforge.libraryapi.dto.UpdateBookRequest;
import com.javaforge.libraryapi.service.LibraryService;
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
@RequestMapping("/api/libraries")
public class LibraryController {
    private final LibraryService service;

    public LibraryController(LibraryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LibraryResponse> create(@Valid @RequestBody CreateLibraryRequest request) {
        LibraryResponse created = service.createLibrary(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<LibraryResponse> findAll() {
        return service.findAllLibraries();
    }

    @GetMapping("/{libraryId}")
    public LibraryResponse findById(@PathVariable long libraryId) {
        return service.findLibrary(libraryId);
    }

    @PutMapping("/{libraryId}")
    public LibraryResponse update(
            @PathVariable long libraryId,
            @Valid @RequestBody CreateLibraryRequest request
    ) {
        return service.updateLibrary(libraryId, request);
    }

    @PostMapping("/{libraryId}/books")
    public ResponseEntity<BookResponse> addBook(
            @PathVariable long libraryId,
            @Valid @RequestBody CreateBookRequest request
    ) {
        BookResponse created = service.addBook(libraryId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{bookId}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{libraryId}/books/{bookId}")
    public BookResponse findBook(@PathVariable long libraryId, @PathVariable long bookId) {
        return service.findBook(libraryId, bookId);
    }

    @PutMapping("/{libraryId}/books/{bookId}")
    public BookResponse updateBook(
            @PathVariable long libraryId,
            @PathVariable long bookId,
            @Valid @RequestBody UpdateBookRequest request
    ) {
        return service.updateBook(libraryId, bookId, request);
    }

    @DeleteMapping("/{libraryId}/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable long libraryId, @PathVariable long bookId) {
        service.deleteBook(libraryId, bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> delete(@PathVariable long libraryId) {
        service.deleteLibrary(libraryId);
        return ResponseEntity.noContent().build();
    }
}
