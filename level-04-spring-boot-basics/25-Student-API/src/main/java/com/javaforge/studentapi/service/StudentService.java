package com.javaforge.studentapi.service;

import com.javaforge.studentapi.dto.CreateStudentRequest;
import com.javaforge.studentapi.dto.UpdateStudentRequest;
import com.javaforge.studentapi.exception.StudentNotFoundException;
import com.javaforge.studentapi.model.Student;
import com.javaforge.studentapi.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student create(CreateStudentRequest request) {
        Instant now = Instant.now();
        return repository.save(new Student(null, request.fullName().trim(),
                normalizeEmail(request.email()), request.age(), request.course().trim(), now, now));
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Student findById(long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student update(long id, UpdateStudentRequest request) {
        Student existing = findById(id);
        return repository.save(new Student(existing.id(), request.fullName().trim(),
                normalizeEmail(request.email()), request.age(), request.course().trim(),
                existing.createdAt(), Instant.now()));
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new StudentNotFoundException(id);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
