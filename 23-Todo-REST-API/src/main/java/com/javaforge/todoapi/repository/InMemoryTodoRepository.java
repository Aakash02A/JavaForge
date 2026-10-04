package com.javaforge.todoapi.repository;

import com.javaforge.todoapi.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTodoRepository implements TodoRepository {
    private final ConcurrentMap<Long, Todo> todos = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Todo save(Todo todo) {
        long id = todo.id() == null ? nextId.getAndIncrement() : todo.id();
        Todo saved = new Todo(id, todo.title(), todo.description(), todo.completed(),
                todo.createdAt(), todo.updatedAt());
        todos.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Todo> findById(long id) {
        return Optional.ofNullable(todos.get(id));
    }

    @Override
    public List<Todo> findAll() {
        return todos.values().stream()
                .sorted(Comparator.comparing(Todo::id))
                .toList();
    }

    @Override
    public boolean deleteById(long id) {
        return todos.remove(id) != null;
    }
}
