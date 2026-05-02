package com.example.demo.service;

import com.example.demo.dto.ReceptionistCreateDTO;
import com.example.demo.model.Receptionist;
import com.example.demo.model.User;
import com.example.demo.repository.ReceptionistRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReceptionistService {
    private final ReceptionistRepository receptionistRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ReceptionistService(ReceptionistRepository receptionistRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.receptionistRepository = receptionistRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Receptionist> findAll() {
        return receptionistRepository.findAll();
    }

    public Optional<Receptionist> findById(Long id) {
        return receptionistRepository.findById(id);
    }

    @Transactional
    public Receptionist createFromDTO(ReceptionistCreateDTO dto) {
        // Check if email already exists
        if (userRepository.findByEmail(dto.getUser().getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create User
        User user = new User();
        user.setEmail(dto.getUser().getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getUser().getPasswordHash()));
        user.setRole(com.example.demo.model.Role.RECEPTIONIST);
        user.setName(dto.getFirstName() + " " + dto.getLastName());
        user.setPhone(dto.getPhonenumber());
        user.setProfilePicture(dto.getProfilePicture());
        user = userRepository.save(user);

        // Create Receptionist
        Receptionist receptionist = new Receptionist();
        receptionist.setUser(user);
        receptionist.setDepartment(dto.getDepartment());
        receptionist.setProfilePicture(dto.getProfilePicture());
        return receptionistRepository.save(receptionist);
    }

    @Transactional
    public Receptionist create(Receptionist receptionist) {
        // Ensure user has RECEPTIONIST role
        if (receptionist.getUser() != null) {
            receptionist.getUser().setRole(com.example.demo.model.Role.RECEPTIONIST);
            userRepository.save(receptionist.getUser());
        }
        return receptionistRepository.save(receptionist);
    }

    @Transactional
    public Receptionist update(Receptionist receptionist) {
        return receptionistRepository.save(receptionist);
    }

    @Transactional
    public void delete(Long id) {
        receptionistRepository.deleteById(id);
    }
}