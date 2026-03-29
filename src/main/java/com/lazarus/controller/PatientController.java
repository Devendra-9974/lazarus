package com.lazarus.controller;

import com.lazarus.model.Patient;
import com.lazarus.service.LazarusService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final LazarusService service;

    public PatientController(LazarusService service) {
        this.service = service;
    }

    @GetMapping
    public List<Patient> getAll() {
        return service.getAllPatients();
    }

    @GetMapping("/{id}")
    public Patient getOne(@PathVariable String id) {
        return service.getById(id);
    }
}

