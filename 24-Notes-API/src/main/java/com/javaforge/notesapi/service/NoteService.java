package com.javaforge.notesapi.service;

import com.javaforge.notesapi.dto.CreateNoteRequest;
import com.javaforge.notesapi.dto.UpdateNoteRequest;
import com.javaforge.notesapi.exception.NoteNotFoundException;
import com.javaforge.notesapi.model.Note;
import com.javaforge.notesapi.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository repository;

    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }

    public Note create(CreateNoteRequest request) {
        Instant now = Instant.now();
        return repository.save(new Note(null, request.title().trim(), request.content().trim(), now, now));
    }

    public List<Note> findAll() {
        return repository.findAll();
    }

    public Note findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    public Note update(long id, UpdateNoteRequest request) {
        Note existing = findById(id);
        return repository.save(new Note(existing.id(), request.title().trim(), request.content().trim(),
                existing.createdAt(), Instant.now()));
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new NoteNotFoundException(id);
        }
    }
}
