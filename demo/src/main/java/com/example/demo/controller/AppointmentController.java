package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AppointmentCreateAnonymousDTO;
import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<Appointment>> getAll() {
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Object doctorObj = body.get("doctor");
            Object patientObj = body.get("patient");
            Long doctorId = null;
            Long patientId = null;

            if (doctorObj instanceof Map<?, ?> doctorMap) {
                Object idValue = doctorMap.get("id");
                if (idValue instanceof Number) doctorId = ((Number) idValue).longValue();
                else if (idValue instanceof String) doctorId = Long.valueOf((String) idValue);
            }
            if (patientObj instanceof Map<?, ?> patientMap) {
                Object idValue = patientMap.get("id");
                if (idValue instanceof Number) patientId = ((Number) idValue).longValue();
                else if (idValue instanceof String) patientId = Long.valueOf((String) idValue);
            }

            String appointmentTime = (String) body.get("appointmentTime");
            String reason = body.containsKey("reason") ? (String) body.get("reason") : null;
            String notes = body.containsKey("notes") ? (String) body.get("notes") : null;

            if (appointmentTime == null || appointmentTime.isBlank()) {
                return ResponseEntity.badRequest().body("appointmentTime is required");
            }

            var time = LocalDateTime.parse(appointmentTime);
            Appointment saved = appointmentService.createAppointment(doctorId, patientId, time, reason, notes);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error creating appointment: " + ex.getMessage());
        }
    }

    @PostMapping("/anonymous")
    public ResponseEntity<?> createAnonymous(@RequestBody AppointmentCreateAnonymousDTO dto) {
        try {
            Appointment saved = appointmentService.createAnonymousAppointment(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return appointmentService.findById(id)
            .map(appointment -> ResponseEntity.ok(appointment))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/checkin")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> checkin(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();

        String username = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) username = ((UserDetails) principal).getUsername();
        else if (principal instanceof String) username = (String) principal;
        if (username == null) return ResponseEntity.status(401).build();

        User current = userService.findByEmail(username).orElse(null);
        if (current == null) return ResponseEntity.status(403).build();

        var opt = appointmentService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Appointment appt = opt.get();

        boolean allowed = false;
        if (current.getRole() == Role.ADMIN) allowed = true;
        else if (current.getRole() == Role.RECEPTIONIST) allowed = true;
        else if (current.getRole() == Role.DOCTOR) {
            if (appt.getDoctor() != null && appt.getDoctor().getUser() != null && appt.getDoctor().getUser().getId().equals(current.getId())) allowed = true;
        }

        if (!allowed) return ResponseEntity.status(403).body("forbidden");

        try {
            Appointment a = appointmentService.checkIn(id);
            return ResponseEntity.ok(a);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        var opt = appointmentService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Appointment existing = opt.get();

        // Update status if provided
        if (body.containsKey("status")) {
            String statusStr = (String) body.get("status");
            try {
                AppointmentStatus status = AppointmentStatus.valueOf(statusStr.toUpperCase());
                existing.setStatus(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status: " + statusStr);
            }
        }

        Appointment updated = appointmentService.update(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        var opt = appointmentService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> confirm(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();

        String username = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) username = ((UserDetails) principal).getUsername();
        else if (principal instanceof String) username = (String) principal;
        if (username == null) return ResponseEntity.status(401).build();

        User current = userService.findByEmail(username).orElse(null);
        if (current == null) return ResponseEntity.status(403).build();

        var opt = appointmentService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Appointment appt = opt.get();

        boolean allowed = false;
        if (current.getRole() == Role.ADMIN) allowed = true;
        else if (current.getRole() == Role.RECEPTIONIST) allowed = true;
        else if (current.getRole() == Role.DOCTOR) {
            if (appt.getDoctor() != null && appt.getDoctor().getUser() != null && appt.getDoctor().getUser().getId().equals(current.getId())) allowed = true;
        }

        if (!allowed) return ResponseEntity.status(403).body("forbidden");

        try {
            String notes = body.containsKey("notes") ? (String) body.get("notes") : null;
            String procedures = body.containsKey("procedures") ? (String) body.get("procedures") : null;
            var visit = appointmentService.confirmAppointment(id, notes, procedures);
            return ResponseEntity.ok(visit);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
