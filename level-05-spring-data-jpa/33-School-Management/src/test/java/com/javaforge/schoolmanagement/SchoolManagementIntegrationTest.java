package com.javaforge.schoolmanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.schoolmanagement.repository.CourseRepository;
import com.javaforge.schoolmanagement.repository.EnrollmentRepository;
import com.javaforge.schoolmanagement.repository.StudentRepository;
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
class SchoolManagementIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void managesStudentsCoursesEnrollmentsAndGrades() throws Exception {
        long studentId = createStudent("Mina Patel", "MINA@school.example");
        long secondStudentId = createStudent("Alex Chen", "alex@school.example");
        long courseId = createCourse("cs101", "Computer Science", 4);

        MvcResult result = mockMvc.perform(post("/api/students/{studentId}/courses/{courseId}",
                        studentId, courseId))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.courseCode").value("CS101"))
                .andExpect(jsonPath("$.grade").doesNotExist())
                .andReturn();
        long enrollmentId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mina@school.example"))
                .andExpect(jsonPath("$.enrollments.length()").value(1));
        mockMvc.perform(get("/api/courses/{id}/enrollments", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(studentId));

        mockMvc.perform(post("/api/students/{studentId}/courses/{courseId}", studentId, courseId))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/students/{studentId}/courses/{courseId}", secondStudentId, courseId))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/students/{studentId}/enrollments/{enrollmentId}", studentId, enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"grade\":95}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(95))
                .andExpect(jsonPath("$.version").value(1));
        mockMvc.perform(put("/api/students/{studentId}/enrollments/{enrollmentId}", studentId, enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"grade\":101}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.grade").exists());

        mockMvc.perform(delete("/api/students/{studentId}/enrollments/{enrollmentId}",
                        studentId, enrollmentId))
                .andExpect(status().isNoContent());
        assertEquals(1, enrollmentRepository.count());
    }

    @Test
    void validatesUniqueStudentEmailsAndCourseCodes() throws Exception {
        createStudent("Mina Patel", "mina@school.example");
        createCourse("CS101", "Computer Science", 4);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentRequest("Duplicate", "MINA@school.example")))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseRequest("cs101", "Duplicate", 3)))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"invalid","dateOfBirth":"2999-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.dateOfBirth").exists());
    }

    @Test
    void deletingStudentOrCourseRemovesDependentEnrollments() throws Exception {
        long studentId = createStudent("Mina Patel", "mina@school.example");
        long courseId = createCourse("CS101", "Computer Science", 4);
        mockMvc.perform(post("/api/students/{studentId}/courses/{courseId}", studentId, courseId))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/courses/{id}", courseId)).andExpect(status().isNoContent());
        assertEquals(0, enrollmentRepository.count());
        mockMvc.perform(get("/api/courses/{id}", courseId)).andExpect(status().isNotFound());

        long secondCourseId = createCourse("MATH201", "Linear Algebra", 3);
        mockMvc.perform(post("/api/students/{studentId}/courses/{courseId}", studentId, secondCourseId))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/api/students/{id}", studentId)).andExpect(status().isNoContent());
        assertEquals(0, enrollmentRepository.count());
        assertEquals(0, studentRepository.count());
    }

    private long createStudent(String name, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentRequest(name, email)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("id").asLong();
    }

    private long createCourse(String code, String title, int credits) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseRequest(code, title, credits)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("id").asLong();
    }

    private String studentRequest(String name, String email) {
        return """
                {"name":"%s","email":"%s","dateOfBirth":"2005-06-15"}
                """.formatted(name, email);
    }

    private String courseRequest(String code, String title, int credits) {
        return """
                {"code":"%s","title":"%s","credits":%d}
                """.formatted(code, title, credits);
    }
}
