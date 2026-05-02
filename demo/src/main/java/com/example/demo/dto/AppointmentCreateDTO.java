package com.example.demo.dto;

import java.time.LocalDateTime;

public class AppointmentCreateDTO {
    private Long doctorId;
    private Long userId; // Changed from patientId to userId
    private LocalDateTime appointmentTime;
    private String reason;
    private String notes;

    public AppointmentCreateDTO() {}

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getUserId() { return userId; } // Changed from getPatientId
    public void setUserId(Long userId) { this.userId = userId; } // Changed from setPatientId

    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}