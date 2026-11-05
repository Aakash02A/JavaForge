package com.javaforge.todoapi.controller;

import com.javaforge.todoapi.exception.ApiExceptionHandler;
import com.javaforge.todoapi.exception.TodoNotFoundException;
import com.javaforge.todoapi.model.Todo;
import com.javaforge.todoapi.service.TodoService;
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

@WebMvcTest(TodoController.class)
@Import(ApiExceptionHandler.class)
class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService service;

    @Test
    void createsTodoAndReturnsLocationHeader() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        when(service.create(any())).thenReturn(
                new Todo(5L, "Read", "", false, timestamp, timestamp));

        mockMvc.perform(post("/api/todos")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":"Read"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/todos/5"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Read"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void rejectsInvalidCreateRequestWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"title":"  "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.title").value("Title is required"));
    }

    @Test
    void returnsStructuredNotFoundResponse() throws Exception {
        when(service.findById(eq(99L))).thenThrow(new TodoNotFoundException(99));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Todo with id 99 was not found"))
                .andExpect(jsonPath("$.path").value("/api/todos/99"))
                .andExpect(jsonPath("$.validationErrors").isEmpty());
    }
}
