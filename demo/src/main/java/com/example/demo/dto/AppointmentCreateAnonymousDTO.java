package com.example.demo.dto;

import java.time.LocalDateTime;

public class AppointmentCreateAnonymousDTO {
    private Long doctorId;
    private String patientName;
    private Integer patientAge;
    private String patientGender;
    private String patientPhone;
    private LocalDateTime appointmentTime;
    private String consultationNeeds;

    public AppointmentCreateAnonymousDTO() {}

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Integer getPatientAge() { return patientAge; }
    public void setPatientAge(Integer patientAge) { this.patientAge = patientAge; }

    public String getPatientGender() { return patientGender; }
    public void setPatientGender(String patientGender) { this.patientGender = patientGender; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getConsultationNeeds() { return consultationNeeds; }
    public void setConsultationNeeds(String consultationNeeds) { this.consultationNeeds = consultationNeeds; }
}