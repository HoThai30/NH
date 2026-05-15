package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Appointment;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.VisitRecord;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.UserService;
import com.example.demo.service.VisitRecordService;

@RestController
@RequestMapping("/visits")
public class VisitController {
    private final VisitRecordService visitRecordService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    public VisitController(VisitRecordService visitRecordService, AppointmentService appointmentService, UserService userService) {
        this.visitRecordService = visitRecordService;
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> create(@RequestBody VisitRecord vr) {
        if (vr.getAppointment() == null || vr.getAppointment().getId() == null) {
            return ResponseEntity.badRequest().body("appointment required");
        }

        var optAppt = appointmentService.findById(vr.getAppointment().getId());
        if (optAppt.isEmpty()) return ResponseEntity.badRequest().body("appointment not found");
        Appointment appt = optAppt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();

        String username = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) username = ((UserDetails) principal).getUsername();
        else if (principal instanceof String) username = (String) principal;
        if (username == null) return ResponseEntity.status(401).build();

        User current = userService.findByEmail(username).orElse(null);
        if (current == null) return ResponseEntity.status(403).build();

        boolean allowed = false;
        if (current.getRole() == Role.ADMIN) allowed = true;
        else if (current.getRole() == Role.DOCTOR) {
            if (appt.getDoctor() != null && appt.getDoctor().getUser() != null && appt.getDoctor().getUser().getId().equals(current.getId())) allowed = true;
        }

        if (!allowed) return ResponseEntity.status(403).body("forbidden: only assigned doctor or admin can create visit record");

        // prevent duplicate visit records for same appointment
        if (visitRecordService.findByAppointment(appt).isPresent()) {
            return ResponseEntity.status(409).body("visit record already exists for this appointment");
        }

        // ensure the persisted appointment is set on the visit record
        vr.setAppointment(appt);
        VisitRecord saved = visitRecordService.createWithAttachments(vr);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return visitRecordService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody VisitRecord body) {
        var optVisit = visitRecordService.findById(id);
        if (optVisit.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        VisitRecord existing = optVisit.get();
        
        // Update fields
        if (body.getNotes() != null) {
            existing.setNotes(body.getNotes());
        }
        if (body.getProcedures() != null) {
            existing.setProcedures(body.getProcedures());
        }
        if (body.getCost() != null) {
            existing.setCost(body.getCost());
        }
        if (body.getPatient() != null) {
            existing.setPatient(body.getPatient());
        }

        VisitRecord updated = visitRecordService.update(existing);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> getAll(@RequestParam(required = false) String filterDate) {
        try {
            // If filterDate is provided, filter by creation date for that specific day
            if (filterDate != null && !filterDate.isBlank()) {
                LocalDate date = LocalDate.parse(filterDate);
                LocalDateTime startDateTime = date.atStartOfDay();
                LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
                
                System.out.println("🔍 Backend Filter Debug:");
                System.out.println("   Filter Date Input: " + filterDate);
                System.out.println("   Start DateTime: " + startDateTime);
                System.out.println("   End DateTime: " + endDateTime);
                
                var results = visitRecordService.findByDate(startDateTime, endDateTime);
                System.out.println("   Results Count: " + results.size());
                if (!results.isEmpty()) {
                    System.out.println("   First Result CreatedAt: " + results.get(0).getCreatedAt());
                }
                
                return ResponseEntity.ok(results);
            }
            
            // If no date parameter, return all visits sorted by most recent first
            var allResults = visitRecordService.findAll();
            System.out.println("🔍 No filter - returning all: " + allResults.size() + " records");
            return ResponseEntity.ok(allResults);
        } catch (Exception e) {
            System.err.println("❌ Filter Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD: " + e.getMessage());
        }
    }
}

