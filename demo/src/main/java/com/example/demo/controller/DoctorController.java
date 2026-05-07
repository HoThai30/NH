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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DoctorCreateDTO;
import com.example.demo.dto.DoctorUpdateDTO;
import com.example.demo.model.Doctor;
import com.example.demo.service.DoctorService;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAll() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    /**
     * Create a new doctor
     * Accepts DoctorCreateDTO with nested User information
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody DoctorCreateDTO dto) {
        try {
            Doctor created = doctorService.createFromDTO(dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error creating doctor: " + ex.getMessage());
        }
    }

    /**
     * Update an existing doctor
     * Accepts DoctorUpdateDTO with partial updates allowed
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DoctorUpdateDTO dto) {
        try {
            Doctor updated = doctorService.updateFromDTO(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error updating doctor: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return doctorService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

//    /**
//     * Test DELETE endpoint - temporarily without auth
//     */
//    @RequestMapping(value = "/test-delete/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity<?> testDelete(@PathVariable Long id) {
//        try {
//            doctorService.deleteById(id);
//            return ResponseEntity.ok("Doctor deleted successfully");
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
//        } catch (Exception ex) {
//            return ResponseEntity.status(500).body("Error deleting doctor: " + ex.getMessage());
//        }
//    }

    /**
     * Delete a doctor by ID (POST method for compatibility)
     */
//    @PostMapping("/delete/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
//    public ResponseEntity<?> deleteViaPost(@PathVariable Long id) {
//        try {
//            doctorService.deleteById(id);
//            return ResponseEntity.ok("Doctor deleted successfully");
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
//        } catch (Exception ex) {
//            return ResponseEntity.status(500).body("Error deleting doctor: " + ex.getMessage());
//        }
//    }

    /**
     * Delete a doctor by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            doctorService.deleteById(id);
            return ResponseEntity.ok("Doctor deleted successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error deleting doctor: " + ex.getMessage());
        }
    }
}
