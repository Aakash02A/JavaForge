package com.javaforge.studentapi.controller;

import com.javaforge.studentapi.exception.ApiExceptionHandler;
import com.javaforge.studentapi.exception.DuplicateStudentEmailException;
import com.javaforge.studentapi.exception.StudentNotFoundException;
import com.javaforge.studentapi.model.Student;
import com.javaforge.studentapi.service.StudentService;
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

@WebMvcTest(StudentController.class)
@Import(ApiExceptionHandler.class)
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService service;

    @Test
    void createsStudentAndReturnsLocationHeader() throws Exception {
        Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        when(service.create(any())).thenReturn(
                new Student(7L, "Asha Rao", "asha@example.com", 20, "Computer Science",
                        timestamp, timestamp));

        mockMvc.perform(post("/api/students")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"fullName":"Asha Rao","email":"asha@example.com","age":20,"course":"Computer Science"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/students/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("asha@example.com"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void rejectsInvalidStudentFieldsWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"fullName":" ","email":"not-an-email","age":12,"course":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.fullName").value("Full name is required"))
                .andExpect(jsonPath("$.validationErrors.email").value("Email must be valid"))
                .andExpect(jsonPath("$.validationErrors.age").value("Age must be at least 16"))
                .andExpect(jsonPath("$.validationErrors.course").value("Course is required"));
    }

    @Test
    void returnsConflictWhenEmailIsAlreadyRegistered() throws Exception {
        when(service.create(any())).thenThrow(new DuplicateStudentEmailException("asha@example.com"));

        mockMvc.perform(post("/api/students")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"fullName":"Asha Rao","email":"asha@example.com","age":20,"course":"Computer Science"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.validationErrors").isEmpty());
    }

    @Test
    void returnsStructuredNotFoundResponse() throws Exception {
        when(service.findById(eq(99L))).thenThrow(new StudentNotFoundException(99));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Student with id 99 was not found"))
                .andExpect(jsonPath("$.path").value("/api/students/99"));
    }
}
