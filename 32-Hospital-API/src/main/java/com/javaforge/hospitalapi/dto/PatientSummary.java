package com.javaforge.hospitalapi.dto;

import com.javaforge.hospitalapi.model.Patient;

public record PatientSummary(Long id, String name, String email) {
    public static PatientSummary from(Patient patient) {
        return new PatientSummary(patient.getId(), patient.getName(), patient.getEmail());
    }
}
