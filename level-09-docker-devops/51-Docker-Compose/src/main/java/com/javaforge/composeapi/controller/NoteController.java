package com.javaforge.composeapi.controller;

import com.javaforge.composeapi.dto.NoteRequest;
import com.javaforge.composeapi.model.Note;
import com.javaforge.composeapi.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Note> create(@Valid @RequestBody NoteRequest request) {
        Note note = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(note.getId())
                .toUri();
        return ResponseEntity.created(location).body(note);
    }

    @GetMapping
    public List<Note> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Note findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Note update(@PathVariable long id, @Valid @RequestBody NoteRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
