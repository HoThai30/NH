package com.example.demo.controller;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Attachment;
import com.example.demo.service.AttachmentService;
import com.example.demo.service.FileStorageService;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final FileStorageService fileStorageService;
    private final AttachmentService attachmentService;

    public AttachmentController(FileStorageService fileStorageService, AttachmentService attachmentService) {
        this.fileStorageService = fileStorageService;
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<?> upload(@RequestParam("visitId") Long visitId, @RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.store(file);
            Attachment a = new Attachment();
            a.setUrl(filename);
            a.setType(file.getContentType());
            a.setSize(file.getSize());
            Attachment saved = attachmentService.addToVisit(visitId, a);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> download(@PathVariable Long id) {
        return attachmentService.findById(id).map(att -> {
            try {
                Resource res = fileStorageService.loadAsResource(att.getUrl());
                String contentType = att.getType() != null ? att.getType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + res.getFilename() + "\"")
                        .body(res);
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}
