package com.javaforge.studentapi.service;

import com.javaforge.studentapi.dto.CreateStudentRequest;
import com.javaforge.studentapi.dto.UpdateStudentRequest;
import com.javaforge.studentapi.exception.DuplicateStudentEmailException;
import com.javaforge.studentapi.exception.StudentNotFoundException;
import com.javaforge.studentapi.model.Student;
import com.javaforge.studentapi.repository.InMemoryStudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentServiceTest {
    private StudentService service;

    @BeforeEach
    void setUp() {
        service = new StudentService(new InMemoryStudentRepository());
    }

    @Test
    void createsAndListsStudentsWithNormalizedValues() {
        Student created = service.create(new CreateStudentRequest(
                "  Asha Rao  ", "  ASHA@example.com ", 20, "  Computer Science  "));

        assertEquals("Asha Rao", created.fullName());
        assertEquals("asha@example.com", created.email());
        assertEquals(20, created.age());
        assertEquals("Computer Science", created.course());
        assertEquals(created.createdAt(), created.updatedAt());
        assertEquals(List.of(created), service.findAll());
    }

    @Test
    void updatesStudentAndPreservesCreationTime() {
        Student created = service.create(new CreateStudentRequest(
                "Asha Rao", "asha@example.com", 20, "Computer Science"));
        Student updated = service.update(created.id(), new UpdateStudentRequest(
                "Asha Patel", "asha@example.com", 21, "Data Science"));

        assertEquals(created.id(), updated.id());
        assertEquals("Asha Patel", updated.fullName());
        assertEquals(21, updated.age());
        assertEquals("Data Science", updated.course());
        assertEquals(created.createdAt(), updated.createdAt());
    }

    @Test
    void rejectsDuplicateEmailsIgnoringCase() {
        service.create(new CreateStudentRequest("Asha Rao", "asha@example.com", 20, "Computing"));

        assertThrows(DuplicateStudentEmailException.class,
                () -> service.create(new CreateStudentRequest("Asha Patel", "ASHA@example.com", 21, "Design")));
    }

    @Test
    void deletesStudentAndReportsMissingIds() {
        Student created = service.create(new CreateStudentRequest(
                "Asha Rao", "asha@example.com", 20, "Computing"));
        service.delete(created.id());

        assertTrue(service.findAll().isEmpty());
        assertThrows(StudentNotFoundException.class, () -> service.findById(created.id()));
        assertThrows(StudentNotFoundException.class, () -> service.delete(created.id()));
    }
}
