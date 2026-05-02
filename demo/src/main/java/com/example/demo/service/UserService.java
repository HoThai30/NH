package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerPatient(String name, String email, String phone, String rawPassword) {
        if (email == null) throw new IllegalArgumentException("email required");
        if (userRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email already registered");
        if (phone != null && userRepository.findByPhone(phone).isPresent()) throw new IllegalArgumentException("Phone already registered");

        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPhone(phone);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(Role.PATIENT);
        return userRepository.save(u);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }
}