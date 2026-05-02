package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.VisitRecord;
import com.example.demo.repository.VisitRecordRepository;

@Service
public class VisitRecordService {
    private final VisitRecordRepository visitRecordRepository;

    public VisitRecordService(VisitRecordRepository visitRecordRepository) {
        this.visitRecordRepository = visitRecordRepository;
    }

    @Transactional
    public VisitRecord createOrUpdate(VisitRecord vr) {
        LocalDateTime now = LocalDateTime.now();
        if (vr.getId() == null || vr.getId() == 0) {
            vr.setCreatedAt(now);
        }
        vr.setUpdatedAt(now);
        return visitRecordRepository.save(vr);
    }

    @Transactional
    public VisitRecord createWithAttachments(VisitRecord vr) {
        if (vr.getAppointment() == null || vr.getAppointment().getId() == null) {
            throw new IllegalArgumentException("appointment required");
        }

        // validate attachments and set back-reference so JPA cascade works
        if (vr.getAttachments() != null) {
            var allowedTypes = java.util.Set.of("image/png", "image/jpeg", "application/pdf");
            long maxSize = 10L * 1024 * 1024; // 10 MB
            for (var att : vr.getAttachments()) {
                if (att.getUrl() == null || att.getUrl().isBlank()) throw new IllegalArgumentException("attachment url required");
                if (att.getSize() != null && att.getSize() > maxSize) throw new IllegalArgumentException("attachment too large");
                if (att.getType() != null && !allowedTypes.contains(att.getType())) throw new IllegalArgumentException("unsupported attachment type");
                att.setVisitRecord(vr);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        vr.setCreatedAt(now);
        vr.setUpdatedAt(now);
        return visitRecordRepository.save(vr);
    }

    @Transactional
    public VisitRecord update(VisitRecord vr) {
        vr.setUpdatedAt(LocalDateTime.now());
        return visitRecordRepository.save(vr);
    }

    public Optional<VisitRecord> findByAppointment(com.example.demo.model.Appointment appointment) {
        return visitRecordRepository.findByAppointment(appointment);
    }

    public Optional<VisitRecord> findById(Long id) { return visitRecordRepository.findById(id); }

    public java.util.List<VisitRecord> findAll() { return visitRecordRepository.findAll(); }
}
