package com.javaforge.notesapi.controller;

import com.javaforge.notesapi.exception.ApiExceptionHandler;
import com.javaforge.notesapi.exception.NoteNotFoundException;
import com.javaforge.notesapi.model.Note;
import com.javaforge.notesapi.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(NoteController.class)
@Import(ApiExceptionHandler.class)
class NoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService service;

    @Test
    void createsNoteAndReturnsLocationHeader() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        when(service.create(any())).thenReturn(
                new Note(7L, "Meeting", "Discuss roadmap", timestamp, timestamp));

        mockMvc.perform(post("/api/notes")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":"Meeting","content":"Discuss roadmap"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/notes/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Meeting"))
                .andExpect(jsonPath("$.content").value("Discuss roadmap"));
    }

    @Test
    void rejectsBlankTitleAndContent() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":"  ","content":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.title").value("Title is required"))
                .andExpect(jsonPath("$.validationErrors.content").value("Content is required"));
    }

    @Test
    void returnsStructuredNotFoundResponse() throws Exception {
        when(service.findById(eq(99L))).thenThrow(new NoteNotFoundException(99));

        mockMvc.perform(get("/api/notes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Note with id 99 was not found"))
                .andExpect(jsonPath("$.path").value("/api/notes/99"))
                .andExpect(jsonPath("$.validationErrors").isEmpty());
    }
}
