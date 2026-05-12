package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ReceptionistCreateDTO;
import com.example.demo.model.Receptionist;
import com.example.demo.model.User;
import com.example.demo.repository.ReceptionistRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.CloudinaryValidator;

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
        // Validate required fields
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (dto.getUser() == null || dto.getUser().getEmail() == null || dto.getUser().getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dto.getUser().getPasswordHash() == null || dto.getUser().getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Check if email already exists
        if (userRepository.findByEmail(dto.getUser().getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate profile picture URL if provided
        String profilePictureUrl = null;
        if (dto.getProfilePicture() != null && !dto.getProfilePicture().trim().isEmpty()) {
            profilePictureUrl = validateAndGetImageUrl(dto.getProfilePicture(), "Profile picture");
        }

        // Create User
        User user = new User();
        user.setEmail(dto.getUser().getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getUser().getPasswordHash()));
        user.setRole(com.example.demo.model.Role.RECEPTIONIST);
        user.setName(dto.getFirstName() + " " + dto.getLastName());
        user.setPhone(dto.getPhonenumber());
        if (profilePictureUrl != null) {
            user.setProfilePicture(profilePictureUrl);
        }
        user = userRepository.save(user);

        // Create Receptionist
        Receptionist receptionist = new Receptionist();
        receptionist.setUser(user);
        receptionist.setDepartment(dto.getDepartment());
        if (profilePictureUrl != null) {
            receptionist.setProfilePicture(profilePictureUrl);
        }
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

    /**
     * Validate and process image URL (Cloudinary)
     * @param imageUrl The image URL to validate
     * @param fieldName Field name for error messages
     * @return The validated image URL
     */
    private String validateAndGetImageUrl(String imageUrl, String fieldName) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        String trimmedUrl = imageUrl.trim();
        if (!CloudinaryValidator.isValidImageUrlOrEmpty(trimmedUrl)) {
            throw new IllegalArgumentException(fieldName + " URL không hợp lệ: " + trimmedUrl);
        }

        return trimmedUrl;
    }
}