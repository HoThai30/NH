	package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AppointmentCreateAnonymousDTO;
import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.Doctor;
import com.example.demo.model.VisitRecord;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientProfileRepository;
import com.example.demo.repository.VisitRecordRepository;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final VisitRecordRepository visitRecordRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientProfileRepository patientProfileRepository,
                              VisitRecordRepository visitRecordRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.visitRecordRepository = visitRecordRepository;
    }

    public List<Appointment> findAll() { return appointmentRepository.findAll(); }

    @Transactional
    public Appointment createAnonymousAppointment(AppointmentCreateAnonymousDTO dto) {
        if (dto.getPatientName() == null || dto.getPatientName().trim().isEmpty()) throw new IllegalArgumentException("Patient name required");
        if (dto.getPatientPhone() == null || dto.getPatientPhone().trim().isEmpty()) throw new IllegalArgumentException("Patient phone required");
        if (dto.getAppointmentTime() == null) throw new IllegalArgumentException("Appointment time required");

        LocalDateTime start = dto.getAppointmentTime();
        LocalDateTime end = start.plusMinutes(30); // Default 30-minute appointment

        Appointment a = new Appointment();
        if (dto.getDoctorId() != null) {
            Optional<Doctor> dOpt = doctorRepository.findById(dto.getDoctorId());
            if (dOpt.isEmpty()) throw new IllegalArgumentException("Doctor not found");
            boolean overlap = appointmentRepository.existsByDoctorAndStartTimeLessThanAndEndTimeGreaterThan(dOpt.get(), start, end);
            if (overlap) throw new IllegalStateException("Time slot not available (possible double-booking)");
            a.setDoctor(dOpt.get());
        }
        a.setPatient(null); // No patient profile for anonymous booking
        a.setPatientName(dto.getPatientName());
        a.setPatientAge(dto.getPatientAge());
        a.setPatientGender(dto.getPatientGender());
        a.setPatientPhone(dto.getPatientPhone());
        a.setStartTime(start);
        a.setEndTime(end);
        a.setStatus(AppointmentStatus.PENDING);
        a.setConsultationNeeds(dto.getConsultationNeeds());

        return appointmentRepository.save(a);
    }

    @Transactional
    public Appointment createAppointment(Long doctorId, Long patientProfileId, LocalDateTime start, String consultationNeeds, String notes) {
        if (doctorId == null) throw new IllegalArgumentException("Doctor ID required");
        if (patientProfileId == null) throw new IllegalArgumentException("Patient ID required");
        if (start == null) throw new IllegalArgumentException("Appointment time required");

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        var patientOpt = patientProfileRepository.findById(patientProfileId);
        if (patientOpt.isEmpty()) throw new IllegalArgumentException("Patient profile not found");

        LocalDateTime end = start.plusMinutes(30);
        boolean overlap = appointmentRepository.existsByDoctorAndStartTimeLessThanAndEndTimeGreaterThan(doctor, start, end);
        if (overlap) throw new IllegalStateException("Time slot not available (possible double-booking)");

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patientOpt.get());
        appointment.setPatientName(patientOpt.get().getUser().getName());
        appointment.setPatientPhone(patientOpt.get().getUser().getPhone());
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConsultationNeeds(consultationNeeds);
        appointment.setSource("admin-created");
        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> findById(Long id) { return appointmentRepository.findById(id); }

    @Transactional
    public Appointment checkIn(Long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        a.setStatus(AppointmentStatus.CHECKED_IN);
        return appointmentRepository.save(a);
    }

    @Transactional
    public Appointment update(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void delete(Long id) {
        // First, delete any associated visit record
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        visitRecordRepository.findByAppointment(appointment)
                .ifPresent(visitRecord -> visitRecordRepository.delete(visitRecord));
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public VisitRecord confirmAppointment(Long appointmentId, String notes, String procedures) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Update status to CONFIRMED
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        // Create visit record
        VisitRecord vr = new VisitRecord();
        vr.setAppointment(null); // Don't link to appointment since it remains
        vr.setPatient(appointment.getPatient());
        vr.setNotes(notes != null ? notes : appointment.getConsultationNeeds());
        vr.setProcedures(procedures);
        vr.setCost(BigDecimal.ZERO);

        VisitRecord saved = visitRecordRepository.save(vr);

        // Do not delete the appointment, keep it with CONFIRMED status

        return saved;
    }
}
