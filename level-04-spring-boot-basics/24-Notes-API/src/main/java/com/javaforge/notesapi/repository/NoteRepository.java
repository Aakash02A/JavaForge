package com.javaforge.notesapi.repository;

import com.javaforge.notesapi.model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    Note save(Note note);

    Optional<Note> findById(long id);

    List<Note> findAll();

    boolean deleteById(long id);
}
