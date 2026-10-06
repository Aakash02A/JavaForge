package com.javaforge.schoolmanagement.controller;

import com.javaforge.schoolmanagement.dto.EnrollmentResponse;
import com.javaforge.schoolmanagement.dto.GradeRequest;
import com.javaforge.schoolmanagement.dto.StudentRequest;
import com.javaforge.schoolmanagement.dto.StudentResponse;
import com.javaforge.schoolmanagement.service.SchoolService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@Validated
@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final SchoolService service;

    public StudentController(SchoolService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse created = service.createStudent(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<StudentResponse> findAll() {
        return service.findAllStudents();
    }

    @GetMapping("/{id}")
    public StudentResponse findById(@PathVariable @Positive long id) {
        return service.findStudent(id);
    }

    @PutMapping("/{id}")
    public StudentResponse update(
            @PathVariable @Positive long id, @Valid @RequestBody StudentRequest request
    ) {
        return service.updateStudent(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}/enrollments")
    public List<EnrollmentResponse> listEnrollments(@PathVariable @Positive long studentId) {
        return service.findStudentEnrollments(studentId);
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable @Positive long studentId, @PathVariable @Positive long courseId
    ) {
        EnrollmentResponse created = service.enroll(studentId, courseId);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/students/{studentId}/enrollments/{enrollmentId}")
                .buildAndExpand(studentId, created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{studentId}/enrollments/{enrollmentId}")
    public EnrollmentResponse updateGrade(
            @PathVariable @Positive long studentId,
            @PathVariable @Positive long enrollmentId,
            @Valid @RequestBody GradeRequest request
    ) {
        return service.updateGrade(studentId, enrollmentId, request);
    }

    @DeleteMapping("/{studentId}/enrollments/{enrollmentId}")
    public ResponseEntity<Void> unenroll(
            @PathVariable @Positive long studentId, @PathVariable @Positive long enrollmentId
    ) {
        service.unenroll(studentId, enrollmentId);
        return ResponseEntity.noContent().build();
    }
}
