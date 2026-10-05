package com.javaforge.studentapi.repository;

import com.javaforge.studentapi.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Student save(Student student);

    Optional<Student> findById(long id);

    List<Student> findAll();

    boolean deleteById(long id);
}
