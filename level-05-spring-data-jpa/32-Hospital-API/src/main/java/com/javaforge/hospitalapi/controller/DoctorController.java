package com.javaforge.hospitalapi.controller;

import com.javaforge.hospitalapi.dto.DoctorRequest;
import com.javaforge.hospitalapi.dto.DoctorResponse;
import com.javaforge.hospitalapi.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final HospitalService service;

    public DoctorController(HospitalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> create(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse created = service.createDoctor(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<DoctorResponse> findAll() {
        return service.findAllDoctors();
    }

    @GetMapping("/{id}")
    public DoctorResponse findById(@PathVariable long id) {
        return service.findDoctor(id);
    }

    @PutMapping("/{id}")
    public DoctorResponse update(@PathVariable long id, @Valid @RequestBody DoctorRequest request) {
        return service.updateDoctor(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{doctorId}/patients/{patientId}")
    public DoctorResponse assignPatient(@PathVariable long doctorId, @PathVariable long patientId) {
        return service.assignPatient(doctorId, patientId);
    }

    @DeleteMapping("/{doctorId}/patients/{patientId}")
    public DoctorResponse unassignPatient(@PathVariable long doctorId, @PathVariable long patientId) {
        return service.unassignPatient(doctorId, patientId);
    }
}
