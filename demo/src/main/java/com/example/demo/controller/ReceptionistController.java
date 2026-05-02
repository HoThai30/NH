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

import com.example.demo.dto.ReceptionistCreateDTO;
import com.example.demo.model.Receptionist;
import com.example.demo.service.ReceptionistService;

@RestController
@RequestMapping("/receptionists")
public class ReceptionistController {
    private final ReceptionistService receptionistService;

    public ReceptionistController(ReceptionistService receptionistService) {
        this.receptionistService = receptionistService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Receptionist>> getAll() {
        return ResponseEntity.ok(receptionistService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Receptionist> getById(@PathVariable Long id) {
        return receptionistService.findById(id)
            .map(receptionist -> ResponseEntity.ok(receptionist))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody ReceptionistCreateDTO dto) {
        try {
            Receptionist created = receptionistService.createFromDTO(dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error creating receptionist: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Receptionist receptionist) {
        if (!receptionistService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        receptionist.setId(id);
        Receptionist updated = receptionistService.update(receptionist);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!receptionistService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        receptionistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}