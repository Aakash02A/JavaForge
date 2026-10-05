package com.javaforge.studentapi.repository;

import com.javaforge.studentapi.exception.DuplicateStudentEmailException;
import com.javaforge.studentapi.model.Student;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryStudentRepository implements StudentRepository {
    private final ConcurrentMap<Long, Student> students = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public synchronized Student save(Student student) {
        String normalizedEmail = normalizeEmail(student.email());
        boolean emailAlreadyUsed = students.values().stream()
                .anyMatch(existing -> !existing.id().equals(student.id())
                        && normalizeEmail(existing.email()).equals(normalizedEmail));
        if (emailAlreadyUsed) {
            throw new DuplicateStudentEmailException(student.email());
        }

        long id = student.id() == null ? nextId.getAndIncrement() : student.id();
        Student saved = new Student(id, student.fullName(), normalizedEmail, student.age(), student.course(),
                student.createdAt(), student.updatedAt());
        students.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Student> findById(long id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public List<Student> findAll() {
        return students.values().stream()
                .sorted(Comparator.comparing(Student::id))
                .toList();
    }

    @Override
    public boolean deleteById(long id) {
        return students.remove(id) != null;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
