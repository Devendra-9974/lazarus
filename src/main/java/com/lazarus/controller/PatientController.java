package com.lazarus.controller;

import com.lazarus.model.Patient;
import com.lazarus.service.LazarusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*") // change later to your Vercel URL for production
public class PatientController {

    private final LazarusService service;

    public PatientController(LazarusService service) {
        this.service = service;
    }

    // ✅ Get all patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAll() {
        List<Patient> patients = service.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // ✅ Get single patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getOne(@PathVariable String id) {
        Patient patient = service.getById(id);

        if (patient == null) {
            return ResponseEntity.notFound().build(); // 404 if not found
        }

        return ResponseEntity.ok(patient);
    }

    // ✅ Health check endpoint (for Render testing)
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}