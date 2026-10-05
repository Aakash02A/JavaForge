package com.javaforge.hospitalapi.exception;

public class RelationshipNotFoundException extends RuntimeException {
    public RelationshipNotFoundException(long doctorId, long patientId) {
        super("Patient " + patientId + " is not assigned to doctor " + doctorId);
    }
}
