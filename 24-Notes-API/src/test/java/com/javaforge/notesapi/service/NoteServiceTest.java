package com.javaforge.notesapi.service;

import com.javaforge.notesapi.dto.CreateNoteRequest;
import com.javaforge.notesapi.dto.UpdateNoteRequest;
import com.javaforge.notesapi.exception.NoteNotFoundException;
import com.javaforge.notesapi.model.Note;
import com.javaforge.notesapi.repository.InMemoryNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteServiceTest {
    private NoteService service;

    @BeforeEach
    void setUp() {
        service = new NoteService(new InMemoryNoteRepository());
    }

    @Test
    void createsAndListsNotesWithNormalizedText() {
        Note created = service.create(new CreateNoteRequest("  Meeting  ", "  Discuss roadmap  "));

        assertEquals("Meeting", created.title());
        assertEquals("Discuss roadmap", created.content());
        assertEquals(created.createdAt(), created.updatedAt());
        assertEquals(List.of(created), service.findAll());
    }

    @Test
    void updatesNoteAndPreservesCreationTime() {
        Note created = service.create(new CreateNoteRequest("Draft", "First version"));
        Note updated = service.update(created.id(),
                new UpdateNoteRequest("  Final  ", "  Final version  "));

        assertEquals(created.id(), updated.id());
        assertEquals("Final", updated.title());
        assertEquals("Final version", updated.content());
        assertEquals(created.createdAt(), updated.createdAt());
    }

    @Test
    void deletesNoteAndReportsMissingIds() {
        Note created = service.create(new CreateNoteRequest("Temporary", "Delete me"));
        service.delete(created.id());

        assertTrue(service.findAll().isEmpty());
        assertThrows(NoteNotFoundException.class, () -> service.findById(created.id()));
        assertThrows(NoteNotFoundException.class, () -> service.delete(created.id()));
    }
}
