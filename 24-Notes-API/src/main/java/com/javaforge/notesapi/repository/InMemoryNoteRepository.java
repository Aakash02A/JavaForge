package com.javaforge.notesapi.repository;

import com.javaforge.notesapi.model.Note;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryNoteRepository implements NoteRepository {
    private final ConcurrentMap<Long, Note> notes = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Note save(Note note) {
        long id = note.id() == null ? nextId.getAndIncrement() : note.id();
        Note saved = new Note(id, note.title(), note.content(), note.createdAt(), note.updatedAt());
        notes.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Note> findById(long id) {
        return Optional.ofNullable(notes.get(id));
    }

    @Override
    public List<Note> findAll() {
        return notes.values().stream()
                .sorted(Comparator.comparing(Note::id))
                .toList();
    }

    @Override
    public boolean deleteById(long id) {
        return notes.remove(id) != null;
    }
}
