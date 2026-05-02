package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientProfileRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ReceptionistRepository;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/debug")
public class DebugController {
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ReceptionistRepository receptionistRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DebugController(UserRepository userRepository, 
                          AppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository,
                          ReceptionistRepository receptionistRepository,
                          PatientProfileRepository patientProfileRepository,
                          PostRepository postRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.receptionistRepository = receptionistRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/reset")
    public ResponseEntity<?> resetDatabase() {
        return ResponseEntity.status(501).body(Map.of(
            "error", "Demo reset is disabled. Use the real SQL Server database instead."
        ));
    }

    @PostMapping("/seed")
    public ResponseEntity<?> seedDemoData() {
        return ResponseEntity.status(501).body(Map.of(
            "error", "Demo seed is disabled. Use the real SQL Server database instead."
        ));
    }
}
