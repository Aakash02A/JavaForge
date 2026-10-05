package com.javaforge.libraryapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.libraryapi.repository.BookRepository;
import com.javaforge.libraryapi.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
        libraryRepository.deleteAll();
    }

    @Test
    void persistsLibraryBooksOneToManyAndSupportsCrud() throws Exception {
        MvcResult createdLibrary = mockMvc.perform(post("/api/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Central Library","address":"10 Main Street"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesRegex(
                        "http://localhost/api/libraries/\\d+")))
                .andExpect(jsonPath("$.books").isEmpty())
                .andReturn();
        long libraryId = objectMapper.readTree(createdLibrary.getResponse().getContentAsString())
                .get("id").asLong();

        MvcResult createdBook = mockMvc.perform(post("/api/libraries/{id}/books", libraryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookRequest("Clean Code", "Robert Martin")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesRegex(
                        "http://localhost/api/libraries/" + libraryId + "/books/\\d+")))
                .andExpect(jsonPath("$.libraryId").value(libraryId))
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andReturn();
        long bookId = objectMapper.readTree(createdBook.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(post("/api/libraries/{id}/books", libraryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookRequest("Effective Java", "Joshua Bloch")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/libraries/{id}", libraryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books.length()").value(2))
                .andExpect(jsonPath("$.books[0].libraryId").value(libraryId));
        mockMvc.perform(get("/api/libraries/{libraryId}/books/{bookId}", libraryId, bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"));

        mockMvc.perform(put("/api/libraries/{libraryId}/books/{bookId}", libraryId, bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookRequest("Clean Coder", "Robert Martin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Coder"));

        mockMvc.perform(put("/api/libraries/{id}", libraryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated Library","address":"20 High Street"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Library"))
                .andExpect(jsonPath("$.books.length()").value(2));

        mockMvc.perform(delete("/api/libraries/{id}/books/{bookId}", libraryId, bookId))
                .andExpect(status().isNoContent());
        assertEquals(1, bookRepository.count());

        mockMvc.perform(delete("/api/libraries/{id}", libraryId))
                .andExpect(status().isNoContent());
        assertEquals(0, bookRepository.count());
        assertEquals(0, libraryRepository.count());
    }

    @Test
    void rejectsBooksForUnknownLibraryAndBooksFromOtherLibraries() throws Exception {
        mockMvc.perform(post("/api/libraries/{id}/books", 900L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookRequest("Orphan", "Unknown")))
                .andExpect(status().isNotFound());

        long libraryId = createLibrary("One");
        long otherLibraryId = createLibrary("Two");
        long bookId = createBook(libraryId);

        mockMvc.perform(get("/api/libraries/{libraryId}/books/{bookId}", otherLibraryId, bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void validatesLibraryAndBookRequestFields() throws Exception {
        mockMvc.perform(post("/api/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","address":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.address").exists());

        mockMvc.perform(post("/api/libraries/{id}/books", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","author":" ","isbn":"","publicationYear":1200}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists())
                .andExpect(jsonPath("$.validationErrors.author").exists())
                .andExpect(jsonPath("$.validationErrors.publicationYear").value(
                        "Publication year must be 1450 or later"));
    }

    private long createLibrary(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","address":"1 Main Street"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("id").asLong();
    }

    private long createBook(long libraryId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/libraries/{id}/books", libraryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookRequest("Test Book", "Test Author")))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private String bookRequest(String title, String author) {
        return """
                {"title":"%s","author":"%s","isbn":"978-0-13-235088-4","publicationYear":2020}
                """.formatted(title, author);
    }
}
