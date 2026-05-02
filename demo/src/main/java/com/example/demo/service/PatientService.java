package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.PatientProfile;
import com.example.demo.model.User;
import com.example.demo.repository.PatientProfileRepository;

@Service
public class PatientService {
    private final PatientProfileRepository patientRepo;

    public PatientService(PatientProfileRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Transactional
    public PatientProfile createOrUpdate(PatientProfile profile) {
        LocalDateTime now = LocalDateTime.now();
        if (profile.getId() == null || profile.getId() == 0) {
            profile.setCreatedAt(now);
        }
        profile.setUpdatedAt(now);
        return patientRepo.save(profile);
    }

    public Optional<PatientProfile> findById(Long id) { return patientRepo.findById(id); }

    public List<PatientProfile> findAll() { return patientRepo.findAll(); }

    public Optional<PatientProfile> findByUser(User user) {
        if (user == null || user.getId() == null) return Optional.empty();
        return patientRepo.findByUserId(user.getId());
    }

    @Transactional
    public void delete(Long id) {
        patientRepo.deleteById(id);
    }
}
