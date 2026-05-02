package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.Doctor;
import com.example.demo.model.PatientProfile;
import com.example.demo.model.Post;
import com.example.demo.model.Receptionist;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DentalServiceRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientProfileRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ReceptionistRepository;
import com.example.demo.repository.UserRepository;

public class DemoDataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DemoDataInitializer.class);

    public CommandLineRunner seedDemoData(
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            ReceptionistRepository receptionistRepository,
            PatientProfileRepository patientProfileRepository,
            AppointmentRepository appointmentRepository,
            DentalServiceRepository dentalServiceRepository,
            PostRepository postRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                logger.info("Demo data initialization skipped because users already exist.");
                return;
            }

            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPhone("+840000000001");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            User doctorUser = new User();
            doctorUser.setName("Dr. Linh Nguyen");
            doctorUser.setEmail("doctor@example.com");
            doctorUser.setPhone("+840000000002");
            doctorUser.setPasswordHash(passwordEncoder.encode("doctor"));
            doctorUser.setRole(Role.DOCTOR);
            userRepository.save(doctorUser);

            Doctor doctor = new Doctor();
            doctor.setUser(doctorUser);
            doctor.setSpecialty("Cardiology");
            doctor.setWorkingHours("Mon-Fri 09:00-17:00");
            doctorRepository.save(doctor);

            User receptionistUser = new User();
            receptionistUser.setName("Receptionist Mai");
            receptionistUser.setEmail("receptionist@example.com");
            receptionistUser.setPhone("+840000000004");
            receptionistUser.setPasswordHash(passwordEncoder.encode("receptionist"));
            receptionistUser.setRole(Role.RECEPTIONIST);
            userRepository.save(receptionistUser);

            Receptionist receptionist = new Receptionist();
            receptionist.setUser(receptionistUser);
            receptionist.setDepartment("Front Desk");
            receptionistRepository.save(receptionist);

            User patientUser = new User();
            patientUser.setName("Patient An");
            patientUser.setEmail("patient@example.com");
            patientUser.setPhone("+840000000003");
            patientUser.setPasswordHash(passwordEncoder.encode("patient"));
            patientUser.setRole(Role.PATIENT);
            userRepository.save(patientUser);

            PatientProfile patientProfile = new PatientProfile();
            patientProfile.setUser(patientUser);
            patientProfile.setDob(LocalDate.of(1990, 6, 15));
            patientProfile.setAddress("123 Nguyen Trai, Hanoi");
            patientProfile.setMedicalHistory("No chronic diseases. Regular checkup every year.");
            patientProfile.setAllergies("Penicillin");
            patientProfileRepository.save(patientProfile);

            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setPatient(patientProfile);
            appointment.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
            appointment.setEndTime(appointment.getStartTime().plusMinutes(30));
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointment.setSource("demo-seed");
            appointmentRepository.save(appointment);

            com.example.demo.model.DentalService implant = new com.example.demo.model.DentalService();
            implant.setName("Cắm răng Implant");
            implant.setDescription("Giải pháp phục hồi răng mất với chân răng titanium bền chắc.");
            implant.setPrice(java.math.BigDecimal.valueOf(4500000));
            implant.setActive(true);
            dentalServiceRepository.save(implant);

            com.example.demo.model.DentalService braces = new com.example.demo.model.DentalService();
            braces.setName("Niềng răng");
            braces.setDescription("Niềng răng chỉnh nha thẩm mỹ, phù hợp với nhiều độ tuổi.");
            braces.setPrice(java.math.BigDecimal.valueOf(12000000));
            braces.setActive(true);
            dentalServiceRepository.save(braces);

            com.example.demo.model.DentalService whitening = new com.example.demo.model.DentalService();
            whitening.setName("Tẩy trắng răng");
            whitening.setDescription("Tẩy trắng răng an toàn, hiệu quả nhanh chóng.");
            whitening.setPrice(java.math.BigDecimal.valueOf(2500000));
            whitening.setActive(true);
            dentalServiceRepository.save(whitening);

            Post post1 = new Post();
            post1.setTitle("Chăm sóc răng miệng đúng cách");
            post1.setContent("Hướng dẫn chăm sóc răng miệng hàng ngày để phòng ngừa sâu răng và viêm nướu.");
            post1.setImageUrl("post-care.jpg");
            post1.setPublished(true);
            post1.setActive(true);
            post1.setCreatedAt(LocalDateTime.now().minusDays(2));
            post1.setUpdatedAt(LocalDateTime.now().minusDays(2));
            post1.setAuthor(admin);
            postRepository.save(post1);

            Post post2 = new Post();
            post2.setTitle("Công nghệ Implant mới nhất");
            post2.setContent("Implant 4.0 giúp phục hồi răng mất nhanh chóng, an toàn và thẩm mỹ.");
            post2.setImageUrl("post-implant.jpg");
            post2.setPublished(true);
            post2.setActive(true);
            post2.setCreatedAt(LocalDateTime.now().minusDays(1));
            post2.setUpdatedAt(LocalDateTime.now().minusDays(1));
            post2.setAuthor(admin);
            postRepository.save(post2);

            Post post3 = new Post();
            post3.setTitle("Niềng răng hiệu quả cho mọi lứa tuổi");
            post3.setContent("Những ưu điểm của niềng răng bằng khay trong suốt và kim loại.");
            post3.setImageUrl("post-braces.jpg");
            post3.setPublished(true);
            post3.setActive(true);
            post3.setCreatedAt(LocalDateTime.now());
            post3.setUpdatedAt(LocalDateTime.now());
            post3.setAuthor(admin);
            postRepository.save(post3);

            logger.info("Seeded demo data: admin={}, doctor={}, patient={}, appointment={}, services=[{}, {}, {}], posts=[{}, {}, {}]",
                    admin.getEmail(), doctorUser.getEmail(), patientUser.getEmail(), appointment.getId(),
                    implant.getName(), braces.getName(), whitening.getName(),
                    post1.getTitle(), post2.getTitle(), post3.getTitle());
        };
    }
}
