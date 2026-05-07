package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndStartTimeBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctor(Doctor doctor);

    boolean existsByDoctorAndStartTimeLessThanAndEndTimeGreaterThan(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime);
}
