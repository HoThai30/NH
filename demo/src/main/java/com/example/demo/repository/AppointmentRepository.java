package com.example.demo.repository;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndStartTimeBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);

    boolean existsByDoctorAndStartTimeLessThanAndEndTimeGreaterThan(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime);
}
