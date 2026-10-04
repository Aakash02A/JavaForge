package com.javaforge.todoapi.controller;

import com.javaforge.todoapi.dto.CreateTodoRequest;
import com.javaforge.todoapi.dto.UpdateCompletionRequest;
import com.javaforge.todoapi.dto.UpdateTodoRequest;
import com.javaforge.todoapi.model.Todo;
import com.javaforge.todoapi.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Todo> create(@Valid @RequestBody CreateTodoRequest request) {
        Todo created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<Todo> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Todo findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable long id, @Valid @RequestBody UpdateTodoRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/completion")
    public Todo updateCompletion(
            @PathVariable long id,
            @Valid @RequestBody UpdateCompletionRequest request
    ) {
        return service.updateCompletion(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
