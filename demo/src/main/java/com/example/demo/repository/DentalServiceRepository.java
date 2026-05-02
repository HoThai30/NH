package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.DentalService;

public interface DentalServiceRepository extends JpaRepository<DentalService, Long> {
    List<DentalService> findByActiveTrueOrderByNameAsc();
}
