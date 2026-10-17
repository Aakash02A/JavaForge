package com.javaforge.composeapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.composeapi.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository repository;

    @BeforeEach
    void clearNotes() {
        repository.deleteAll();
    }

    @Test
    void supportsPersistentNoteCrud() throws Exception {
        String created = mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Compose","content":"Connect the API to PostgreSQL"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Compose"))
                .andReturn().getResponse().getContentAsString();
        JsonNode note = objectMapper.readTree(created);
        long id = note.get("id").asLong();

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Connect the API to PostgreSQL"));

        mockMvc.perform(put("/api/notes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Compose updated","content":"Persistent storage"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Compose updated"));

        mockMvc.perform(get("/api/notes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Persistent storage"));

        mockMvc.perform(delete("/api/notes/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsBlankNoteTitle() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"  ","content":"Invalid"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
