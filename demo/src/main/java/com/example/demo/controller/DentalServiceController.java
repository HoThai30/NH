package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.DentalService;
import com.example.demo.service.DentalServiceService;

@RestController
@RequestMapping("/services")
public class DentalServiceController {

    private final DentalServiceService dentalServiceService;

    public DentalServiceController(DentalServiceService dentalServiceService) {
        this.dentalServiceService = dentalServiceService;
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping
    public ResponseEntity<List<DentalService>> getAllActive() {
        return ResponseEntity.ok(dentalServiceService.findAllActive());
    }

    // ================= GET ALL (ADMIN) =================
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<List<DentalService>> getAllAdmin() {
        return ResponseEntity.ok(dentalServiceService.findAll());
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<DentalService> getById(@PathVariable Long id) {
        return dentalServiceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> create(@RequestBody DentalService dentalService) {
        try {
            DentalService created = dentalServiceService.createServiceFromJson(dentalService);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error creating service: " + ex.getMessage());
        }
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DentalService dentalService) {
        try {
            DentalService updated = dentalServiceService.updateServiceFromJson(id, dentalService);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error updating service: " + ex.getMessage());
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (dentalServiceService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        dentalServiceService.deleteService(id);
        return ResponseEntity.ok().build();
    }
}