package com.javaforge.schoolmanagement.controller;

import com.javaforge.schoolmanagement.dto.CourseRequest;
import com.javaforge.schoolmanagement.dto.CourseResponse;
import com.javaforge.schoolmanagement.dto.EnrollmentResponse;
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
@RequestMapping("/api/courses")
public class CourseController {
    private final SchoolService service;

    public CourseController(SchoolService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        CourseResponse created = service.createCourse(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<CourseResponse> findAll() {
        return service.findAllCourses();
    }

    @GetMapping("/{id}")
    public CourseResponse findById(@PathVariable @Positive long id) {
        return service.findCourse(id);
    }

    @PutMapping("/{id}")
    public CourseResponse update(
            @PathVariable @Positive long id, @Valid @RequestBody CourseRequest request
    ) {
        return service.updateCourse(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        service.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/enrollments")
    public List<EnrollmentResponse> findEnrollments(@PathVariable @Positive long courseId) {
        return service.findCourseEnrollments(courseId);
    }
}
