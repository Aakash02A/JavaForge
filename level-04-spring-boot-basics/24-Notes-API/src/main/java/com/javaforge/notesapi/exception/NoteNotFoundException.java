package com.javaforge.notesapi.exception;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(long id) {
        super("Note with id " + id + " was not found");
    }
}
