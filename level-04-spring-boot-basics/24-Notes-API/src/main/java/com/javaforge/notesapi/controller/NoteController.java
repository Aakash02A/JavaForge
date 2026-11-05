package com.javaforge.notesapi.controller;

import com.javaforge.notesapi.dto.CreateNoteRequest;
import com.javaforge.notesapi.dto.UpdateNoteRequest;
import com.javaforge.notesapi.model.Note;
import com.javaforge.notesapi.service.NoteService;
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
    public ResponseEntity<Note> create(@Valid @RequestBody CreateNoteRequest request) {
        Note created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
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
    public Note update(@PathVariable long id, @Valid @RequestBody UpdateNoteRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
