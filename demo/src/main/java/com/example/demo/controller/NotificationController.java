package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Appointment;
import com.example.demo.model.Notification;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AppointmentService appointmentService;

    public NotificationController(NotificationService notificationService, AppointmentService appointmentService) {
        this.notificationService = notificationService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> send(@RequestBody Map<String, String> body) {
        try {
            Long appointmentId = Long.valueOf(body.get("appointmentId"));
            String channel = body.getOrDefault("channel", "email");
            String template = body.getOrDefault("template", "default");
            Appointment a = appointmentService.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("appointment not found"));
            Notification n = notificationService.sendNotification(a, channel, template);
            return ResponseEntity.ok(n);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
