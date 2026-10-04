package com.javaforge.todoapi.service;

import com.javaforge.todoapi.dto.CreateTodoRequest;
import com.javaforge.todoapi.dto.UpdateCompletionRequest;
import com.javaforge.todoapi.dto.UpdateTodoRequest;
import com.javaforge.todoapi.exception.TodoNotFoundException;
import com.javaforge.todoapi.model.Todo;
import com.javaforge.todoapi.repository.InMemoryTodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoServiceTest {
    private TodoService service;

    @BeforeEach
    void setUp() {
        service = new TodoService(new InMemoryTodoRepository());
    }

    @Test
    void createsAndListsTodosWithNormalizedValues() {
        Todo created = service.create(new CreateTodoRequest("  Buy milk  ", null));

        assertEquals("Buy milk", created.title());
        assertEquals("", created.description());
        assertFalse(created.completed());
        assertEquals(created.createdAt(), created.updatedAt());
        assertEquals(List.of(created), service.findAll());
    }

    @Test
    void updatesTodoAndCompletionWithoutChangingCreationTime() {
        Todo created = service.create(new CreateTodoRequest("Draft", " first "));
        Todo updated = service.update(created.id(),
                new UpdateTodoRequest("  Publish  ", " final ", true));

        assertEquals(created.id(), updated.id());
        assertEquals("Publish", updated.title());
        assertEquals("final", updated.description());
        assertTrue(updated.completed());
        assertEquals(created.createdAt(), updated.createdAt());

        Todo reopened = service.updateCompletion(created.id(), new UpdateCompletionRequest(false));
        assertFalse(reopened.completed());
        assertEquals(updated.title(), reopened.title());
    }

    @Test
    void deletesTodoAndRejectsMissingIds() {
        Todo created = service.create(new CreateTodoRequest("Remove me", ""));
        service.delete(created.id());

        assertTrue(service.findAll().isEmpty());
        assertThrows(TodoNotFoundException.class, () -> service.findById(created.id()));
        assertThrows(TodoNotFoundException.class, () -> service.delete(created.id()));
    }
}
