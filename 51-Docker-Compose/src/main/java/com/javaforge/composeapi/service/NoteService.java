package com.javaforge.composeapi.service;

import com.javaforge.composeapi.dto.NoteRequest;
import com.javaforge.composeapi.model.Note;
import com.javaforge.composeapi.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {
    private final NoteRepository repository;

    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }

    public Note create(NoteRequest request) {
        return repository.save(new Note(request.title(), request.content()));
    }

    public List<Note> findAll() {
        return repository.findAll();
    }

    public Note findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    public Note update(long id, NoteRequest request) {
        Note note = findById(id);
        note.setTitle(request.title());
        note.setContent(request.content());
        return repository.save(note);
    }

    public void delete(long id) {
        repository.delete(findById(id));
    }
}
