package com.javaforge.todoapi.service;

import com.javaforge.todoapi.dto.CreateTodoRequest;
import com.javaforge.todoapi.dto.UpdateCompletionRequest;
import com.javaforge.todoapi.dto.UpdateTodoRequest;
import com.javaforge.todoapi.exception.TodoNotFoundException;
import com.javaforge.todoapi.model.Todo;
import com.javaforge.todoapi.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TodoService {
    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public Todo create(CreateTodoRequest request) {
        Instant now = Instant.now();
        return repository.save(new Todo(null, request.title().trim(), normalizeDescription(request.description()),
                false, now, now));
    }

    public List<Todo> findAll() {
        return repository.findAll();
    }

    public Todo findById(long id) {
        return repository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo update(long id, UpdateTodoRequest request) {
        Todo existing = findById(id);
        return repository.save(new Todo(existing.id(), request.title().trim(),
                normalizeDescription(request.description()), request.completed(),
                existing.createdAt(), Instant.now()));
    }

    public Todo updateCompletion(long id, UpdateCompletionRequest request) {
        Todo existing = findById(id);
        return repository.save(new Todo(existing.id(), existing.title(), existing.description(),
                request.completed(), existing.createdAt(), Instant.now()));
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new TodoNotFoundException(id);
        }
    }

    private String normalizeDescription(String description) {
        return description == null ? "" : description.trim();
    }
}
