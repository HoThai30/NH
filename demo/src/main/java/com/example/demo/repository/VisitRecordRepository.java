package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Appointment;
import com.example.demo.model.VisitRecord;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {
	Optional<VisitRecord> findByAppointment(Appointment appointment);
	List<VisitRecord> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
}
