package com.javaforge.todoapi.repository;

import com.javaforge.todoapi.model.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);

    Optional<Todo> findById(long id);

    List<Todo> findAll();

    boolean deleteById(long id);
}
