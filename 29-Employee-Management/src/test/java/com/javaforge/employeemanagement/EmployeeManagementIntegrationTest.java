package com.javaforge.employeemanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeManagementIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        employeeRepository.deleteAll();
    }

    @Test
    void persistsEmployeeAndSupportsCrudLifecycle() throws Exception {
        MvcResult createdResult = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployee("MAYA@example.com", "Maya", "Rao")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesRegex(
                        "http://localhost/api/employees/\\d+")))
                .andExpect(jsonPath("$.email").value("maya@example.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        JsonNode created = objectMapper.readTree(createdResult.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Maya"));
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployee("maya@example.com", "Maya", "Shah")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Shah"));
        mockMvc.perform(delete("/api/employees/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void validatesFieldsBeforePersisting() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":" ","lastName":"","email":"invalid","department":"","jobTitle":"","salary":0,"hireDate":"2999-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.firstName").exists())
                .andExpect(jsonPath("$.validationErrors.email").value("Email must be valid"))
                .andExpect(jsonPath("$.validationErrors.salary").value("Salary must be greater than zero"))
                .andExpect(jsonPath("$.validationErrors.hireDate").value("Hire date cannot be in the future"));
        assertTrue(employeeRepository.findAll().isEmpty());
    }

    @Test
    void rejectsDuplicateEmailCaseInsensitively() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployee("maya@example.com", "Maya", "Rao")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployee("MAYA@example.com", "Maya", "Shah")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        assertEquals(1, employeeRepository.count());
    }

    @Test
    void rejectsEmailAlreadyUsedByAnotherEmployeeDuringUpdate() throws Exception {
        MvcResult first = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployee("one@example.com", "First", "Employee")))
                .andExpect(status().isCreated())
                .andReturn();
        MvcResult second = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployee("two@example.com", "Second", "Employee")))
                .andExpect(status().isCreated())
                .andReturn();
        long secondId = objectMapper.readTree(second.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/employees/{id}", secondId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployee("ONE@example.com", "Second", "Employee")))
                .andExpect(status().isConflict());
        assertEquals(2, employeeRepository.count());
        assertEquals("one@example.com", objectMapper.readTree(first.getResponse().getContentAsString())
                .get("email").asText());
    }

    private String validEmployee(String email, String firstName, String lastName) {
        return """
                {"firstName":"%s","lastName":"%s","email":"%s","department":"Engineering",
                "jobTitle":"Software Engineer","salary":95000.00,"hireDate":"2022-04-15"}
                """.formatted(firstName, lastName, email);
    }
}
