package com.example.demo.service;

import com.example.demo.model.Attachment;
import com.example.demo.model.VisitRecord;
import com.example.demo.repository.AttachmentRepository;
import com.example.demo.repository.VisitRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final VisitRecordRepository visitRecordRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, VisitRecordRepository visitRecordRepository) {
        this.attachmentRepository = attachmentRepository;
        this.visitRecordRepository = visitRecordRepository;
    }

    @Transactional
    public Attachment addToVisit(Long visitRecordId, Attachment attachment) {
        VisitRecord vr = visitRecordRepository.findById(visitRecordId).orElseThrow(() -> new IllegalArgumentException("VisitRecord not found"));
        attachment.setVisitRecord(vr);
        return attachmentRepository.save(attachment);
    }

    public Optional<Attachment> findById(Long id) { return attachmentRepository.findById(id); }
}
