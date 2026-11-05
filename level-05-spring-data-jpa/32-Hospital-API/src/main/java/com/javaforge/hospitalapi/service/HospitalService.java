package com.javaforge.hospitalapi.service;

import com.javaforge.hospitalapi.dto.DoctorRequest;
import com.javaforge.hospitalapi.dto.DoctorResponse;
import com.javaforge.hospitalapi.dto.PatientRequest;
import com.javaforge.hospitalapi.dto.PatientResponse;
import com.javaforge.hospitalapi.exception.DuplicateEmailException;
import com.javaforge.hospitalapi.exception.RelationshipNotFoundException;
import com.javaforge.hospitalapi.exception.ResourceNotFoundException;
import com.javaforge.hospitalapi.model.Doctor;
import com.javaforge.hospitalapi.model.Patient;
import com.javaforge.hospitalapi.repository.DoctorRepository;
import com.javaforge.hospitalapi.repository.PatientRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class HospitalService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public HospitalService(DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public DoctorResponse createDoctor(DoctorRequest request) {
        String email = normalizeEmail(request.email());
        ensureDoctorEmailAvailable(email, null);
        return DoctorResponse.from(doctorRepository.saveAndFlush(
                new Doctor(request.name().trim(), email, request.specialty().trim())));
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> findAllDoctors() {
        return doctorRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(DoctorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponse findDoctor(long id) {
        return DoctorResponse.from(getDoctor(id));
    }

    public DoctorResponse updateDoctor(long id, DoctorRequest request) {
        Doctor doctor = getDoctor(id);
        String email = normalizeEmail(request.email());
        ensureDoctorEmailAvailable(email, id);
        doctor.updateDetails(request.name().trim(), email, request.specialty().trim());
        return DoctorResponse.from(doctorRepository.saveAndFlush(doctor));
    }

    public void deleteDoctor(long id) {
        Doctor doctor = getDoctor(id);
        for (Patient patient : List.copyOf(doctor.getPatients())) {
            doctor.removePatient(patient);
        }
        doctorRepository.delete(doctor);
    }

    public PatientResponse createPatient(PatientRequest request) {
        String email = normalizeEmail(request.email());
        ensurePatientEmailAvailable(email, null);
        Patient patient = new Patient(request.name().trim(), email, request.dateOfBirth());
        return PatientResponse.from(patientRepository.saveAndFlush(patient));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> findAllPatients() {
        return patientRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(PatientResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse findPatient(long id) {
        return PatientResponse.from(getPatient(id));
    }

    public PatientResponse updatePatient(long id, PatientRequest request) {
        Patient patient = getPatient(id);
        String email = normalizeEmail(request.email());
        ensurePatientEmailAvailable(email, id);
        patient.updateDetails(request.name().trim(), email, request.dateOfBirth());
        return PatientResponse.from(patientRepository.saveAndFlush(patient));
    }

    public void deletePatient(long id) {
        Patient patient = getPatient(id);
        for (Doctor doctor : List.copyOf(patient.getDoctors())) {
            doctor.removePatient(patient);
        }
        patientRepository.delete(patient);
    }

    public DoctorResponse assignPatient(long doctorId, long patientId) {
        Doctor doctor = getDoctor(doctorId);
        Patient patient = getPatient(patientId);
        doctor.assignPatient(patient);
        return DoctorResponse.from(doctorRepository.saveAndFlush(doctor));
    }

    public DoctorResponse unassignPatient(long doctorId, long patientId) {
        Doctor doctor = getDoctor(doctorId);
        Patient patient = getPatient(patientId);
        if (!doctor.getPatients().contains(patient)) {
            throw new RelationshipNotFoundException(doctorId, patientId);
        }
        doctor.removePatient(patient);
        return DoctorResponse.from(doctorRepository.saveAndFlush(doctor));
    }

    private Doctor getDoctor(long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    private Patient getPatient(long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    private void ensureDoctorEmailAvailable(String email, Long currentId) {
        doctorRepository.findByEmail(email).filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateEmailException(email);
                });
    }

    private void ensurePatientEmailAvailable(String email, Long currentId) {
        patientRepository.findByEmail(email).filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateEmailException(email);
                });
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
