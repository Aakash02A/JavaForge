package com.javaforge.composeapi.repository;

import com.javaforge.composeapi.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
