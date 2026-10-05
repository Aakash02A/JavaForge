package com.javaforge.libraryapi.service;

import com.javaforge.libraryapi.dto.BookResponse;
import com.javaforge.libraryapi.dto.CreateBookRequest;
import com.javaforge.libraryapi.dto.CreateLibraryRequest;
import com.javaforge.libraryapi.dto.LibraryResponse;
import com.javaforge.libraryapi.dto.UpdateBookRequest;
import com.javaforge.libraryapi.exception.BookNotFoundException;
import com.javaforge.libraryapi.exception.LibraryNotFoundException;
import com.javaforge.libraryapi.model.Book;
import com.javaforge.libraryapi.model.Library;
import com.javaforge.libraryapi.repository.BookRepository;
import com.javaforge.libraryapi.repository.LibraryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;

    public LibraryService(LibraryRepository libraryRepository, BookRepository bookRepository) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
    }

    public LibraryResponse createLibrary(CreateLibraryRequest request) {
        Library library = new Library(request.name().trim(), request.address().trim());
        return LibraryResponse.from(libraryRepository.saveAndFlush(library));
    }

    @Transactional(readOnly = true)
    public List<LibraryResponse> findAllLibraries() {
        return libraryRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(LibraryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibraryResponse findLibrary(long id) {
        return LibraryResponse.from(getLibrary(id));
    }

    public LibraryResponse updateLibrary(long id, CreateLibraryRequest request) {
        Library library = getLibrary(id);
        library.updateDetails(request.name().trim(), request.address().trim());
        return LibraryResponse.from(libraryRepository.saveAndFlush(library));
    }

    public BookResponse addBook(long libraryId, CreateBookRequest request) {
        Library library = getLibrary(libraryId);
        library.addBook(request.title().trim(), request.author().trim(), normalizeIsbn(request.isbn()),
                request.publicationYear());
        Library savedLibrary = libraryRepository.saveAndFlush(library);
        return BookResponse.from(savedLibrary.getBooks().getLast());
    }

    @Transactional(readOnly = true)
    public BookResponse findBook(long libraryId, long bookId) {
        if (!libraryRepository.existsById(libraryId)) {
            throw new LibraryNotFoundException(libraryId);
        }
        Book book = bookRepository.findByIdAndLibraryId(bookId, libraryId)
                .orElseThrow(() -> new BookNotFoundException(bookId, libraryId));
        return BookResponse.from(book);
    }

    public BookResponse updateBook(long libraryId, long bookId, UpdateBookRequest request) {
        Library library = getLibrary(libraryId);
        Book book = bookRepository.findByIdAndLibraryId(bookId, libraryId)
                .orElseThrow(() -> new BookNotFoundException(bookId, libraryId));
        book.updateDetails(request.title().trim(), request.author().trim(),
                normalizeIsbn(request.isbn()), request.publicationYear());
        return BookResponse.from(bookRepository.saveAndFlush(book));
    }

    public void deleteBook(long libraryId, long bookId) {
        Library library = getLibrary(libraryId);
        Book book = bookRepository.findByIdAndLibraryId(bookId, libraryId)
                .orElseThrow(() -> new BookNotFoundException(bookId, libraryId));
        library.removeBook(book);
        libraryRepository.saveAndFlush(library);
    }

    public void deleteLibrary(long id) {
        libraryRepository.delete(getLibrary(id));
    }

    private Library getLibrary(long id) {
        return libraryRepository.findById(id).orElseThrow(() -> new LibraryNotFoundException(id));
    }

    private String normalizeIsbn(String isbn) {
        return isbn.trim().replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
    }
}
