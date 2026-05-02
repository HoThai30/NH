package com.example.demo.repository;

import com.example.demo.model.Appointment;
import com.example.demo.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {
	Optional<VisitRecord> findByAppointment(Appointment appointment);
}
