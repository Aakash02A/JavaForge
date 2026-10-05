package com.javaforge.hospitalapi.dto;

import com.javaforge.hospitalapi.model.Patient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PatientResponse(
        Long id,
        String name,
        String email,
        LocalDate dateOfBirth,
        List<DoctorSummary> doctors,
        Instant createdAt,
        Instant updatedAt
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getName(), patient.getEmail(),
                patient.getDateOfBirth(), patient.getDoctors().stream().map(DoctorSummary::from).toList(),
                patient.getCreatedAt(), patient.getUpdatedAt());
    }
}
