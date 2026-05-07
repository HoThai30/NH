package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.DoctorCreateDTO;
import com.example.demo.dto.DoctorUpdateDTO;
import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.UserRepository;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository, AppointmentRepository appointmentRepository, BCryptPasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new doctor from DoctorCreateDTO
     * This is the PRIMARY create method
     */
    @Transactional
    public Doctor createFromDTO(DoctorCreateDTO dto) {
        // Validate required fields
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (dto.getSpecialization() == null || dto.getSpecialization().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization is required");
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
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

        // Check if phone already exists
        if (userRepository.findByPhone(dto.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create and save User
        User user = new User();
        user.setName(dto.getFirstName() + " " + dto.getLastName());
        user.setEmail(dto.getUser().getEmail());
        user.setPhone(dto.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(dto.getUser().getPasswordHash()));
        user.setRole(Role.DOCTOR);

        String pictureValue = null;
        if (dto.getUser().getProfilePicture() != null && !dto.getUser().getProfilePicture().isEmpty()) {
            pictureValue = dto.getUser().getProfilePicture();
        }
        if ((dto.getProfilePicture() != null && !dto.getProfilePicture().isEmpty()) && pictureValue == null) {
            pictureValue = dto.getProfilePicture();
        }
        if (pictureValue != null) {
            user.setProfilePicture(pictureValue);
        }

        User savedUser = userRepository.save(user);

        // Create and save Doctor
        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setSpecialty(dto.getSpecialization());
        doctor.setProfilePicture(pictureValue);

        return doctorRepository.save(doctor);
    }

    /**
     * Update an existing doctor from DoctorUpdateDTO
     * This is the PRIMARY update method
     */
    @Transactional
    public Doctor updateFromDTO(Long doctorId, DoctorUpdateDTO dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        User user = doctor.getUser();
        if (user == null) {
            throw new IllegalArgumentException("Doctor user relationship is broken");
        }

        // Update name if provided
        if (dto.getFirstName() != null || dto.getLastName() != null) {
            String firstName = dto.getFirstName() != null ? dto.getFirstName() : "";
            String lastName = dto.getLastName() != null ? dto.getLastName() : "";
            user.setName((firstName + " " + lastName).trim());
        }

        // Update email if provided and not already used
        if (dto.getUser() != null && dto.getUser().getEmail() != null && !dto.getUser().getEmail().isEmpty()) {
            if (!dto.getUser().getEmail().equals(user.getEmail())) {
                if (userRepository.findByEmail(dto.getUser().getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email already exists");
                }
                user.setEmail(dto.getUser().getEmail());
            }
        }

        // Update phone if provided and not already used
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            if (!dto.getPhoneNumber().equals(user.getPhone())) {
                if (userRepository.findByPhone(dto.getPhoneNumber()).isPresent()) {
                    throw new IllegalArgumentException("Phone number already exists");
                }
                user.setPhone(dto.getPhoneNumber());
            }
        }

        String pictureValue = null;
        if (dto.getUser() != null && dto.getUser().getProfilePicture() != null && !dto.getUser().getProfilePicture().isEmpty()) {
            pictureValue = dto.getUser().getProfilePicture();
            user.setProfilePicture(pictureValue);
        }

        if (dto.getProfilePicture() != null && !dto.getProfilePicture().isEmpty()) {
            pictureValue = dto.getProfilePicture();
            doctor.setProfilePicture(dto.getProfilePicture());
            if (user.getProfilePicture() == null || user.getProfilePicture().isEmpty()) {
                user.setProfilePicture(dto.getProfilePicture());
            }
        } else if (pictureValue != null) {
            doctor.setProfilePicture(pictureValue);
        }

        if (dto.getSpecialization() != null && !dto.getSpecialization().isEmpty()) {
            doctor.setSpecialty(dto.getSpecialization());
        }

        userRepository.save(user);
        return doctorRepository.save(doctor);
    }

    /**
     * Legacy method - kept for backward compatibility
     * Use createFromDTO instead
     */
    @Transactional
    public Doctor createOrUpdate(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    /**
     * Legacy method - kept for backward compatibility
     * Use createFromDTO instead
     */
    @Transactional
    public Doctor createDoctor(String name, String email, String phone, String specialty, String workingHours, String profilePicture, String password) {
        // Validate input
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Doctor name required");
        if (phone == null || phone.trim().isEmpty()) throw new IllegalArgumentException("Doctor phone required");
        if (specialty == null || specialty.trim().isEmpty()) throw new IllegalArgumentException("Doctor specialty required");

        // Check if email already exists
        if (email != null && !email.trim().isEmpty()) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        // Check if phone already exists
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new IllegalArgumentException("Phone already exists");
        }

        // Create User
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(password != null ? passwordEncoder.encode(password) : passwordEncoder.encode("temp-password")); // Default password
        user.setRole(Role.DOCTOR);
        User savedUser = userRepository.save(user);

        // Create Doctor
        Doctor newDoctor = new Doctor();
        newDoctor.setUser(savedUser);
        newDoctor.setSpecialty(specialty);
        newDoctor.setWorkingHours(workingHours);
        newDoctor.setProfilePicture(profilePicture);

        return doctorRepository.save(newDoctor);
    }

    public Optional<Doctor> findById(Long id) { return doctorRepository.findById(id); }

    public List<Doctor> findAll() { return doctorRepository.findAll(); }

    /**
     * Delete a doctor by ID
     */
    @Transactional
    public void deleteById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        // Find all appointments for this doctor and set doctor to null
        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);

        for (Appointment appointment : appointments) {
            appointment.setDoctor(null);
            appointmentRepository.save(appointment);
        }

        // Flush to ensure appointments are updated before deleting doctor
        appointmentRepository.flush();

        // Instead of deleting user, change role to indicate this user is no longer a doctor
        if (doctor.getUser() != null) {
            User user = doctor.getUser();
            user.setRole(Role.PATIENT); // Change role to patient
            user.setName(user.getName() + " (Former Doctor)");
            userRepository.save(user);
        }

        // Delete doctor record only
        doctorRepository.delete(doctor);
    }
}
