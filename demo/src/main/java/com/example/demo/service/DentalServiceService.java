package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.DentalService;
import com.example.demo.repository.DentalServiceRepository;

@Service
public class DentalServiceService {

    private final DentalServiceRepository dentalServiceRepository;
    private final FileStorageService fileStorageService;

    public DentalServiceService(DentalServiceRepository dentalServiceRepository,
                                FileStorageService fileStorageService) {
        this.dentalServiceRepository = dentalServiceRepository;
        this.fileStorageService = fileStorageService;
    }

    // ================= GET =================
    public List<DentalService> findAllActive() {
        return dentalServiceRepository.findByActiveTrueOrderByNameAsc();
    }

    public List<DentalService> findAll() {
        return dentalServiceRepository.findAll();
    }

    public Optional<DentalService> findById(Long id) {
        return dentalServiceRepository.findById(id);
    }

    // ================= CREATE =================
    @Transactional
    public DentalService createService(DentalService dentalService, MultipartFile file) {
        try {
            // xử lý ảnh
            if (file != null && !file.isEmpty()) {
                String filename = fileStorageService.store(file);
                dentalService.setImgService(filename);
            }

            return dentalServiceRepository.save(dentalService);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo service: " + e.getMessage());
        }
    }

    // ================= UPDATE =================
    @Transactional
    public DentalService updateService(Long id, DentalService dentalService, MultipartFile file) {

        DentalService existing = dentalServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service không tồn tại"));

        try {
            // Update fields
            existing.setName(dentalService.getName());
            existing.setDescription(dentalService.getDescription());
            existing.setPrice(dentalService.getPrice());
            existing.setActive(dentalService.getActive());

            // xử lý ảnh
            if (file != null && !file.isEmpty()) {
                String filename = fileStorageService.store(file);
                existing.setImgService(filename);
            }
            // keep existing img if no new file

            return dentalServiceRepository.save(existing);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi update service: " + e.getMessage());
        }
    }

    // ================= DELETE =================
    @Transactional
    public void deleteService(Long id) {
        if (!dentalServiceRepository.existsById(id)) {
            throw new RuntimeException("Service không tồn tại");
        }
        dentalServiceRepository.deleteById(id);
    }
}