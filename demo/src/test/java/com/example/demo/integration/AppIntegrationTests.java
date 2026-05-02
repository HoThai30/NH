package com.example.demo.integration;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.Doctor;
import com.example.demo.model.PatientProfile;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientProfileRepository;
import com.example.demo.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppIntegrationTests {
    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void registerCreateAppointmentAndCheckin() {
        // Register patient
        Map<String, String> body = Map.of("name", "Test Patient", "email", "patient@example.com", "phone", "012345", "password", "secret");
        ResponseEntity<Map> regResp = restTemplate.postForEntity(baseUrl("/auth/register"), body, Map.class);
        Assertions.assertEquals(HttpStatus.OK, regResp.getStatusCode());
        Map reg = regResp.getBody();
        Assertions.assertNotNull(reg);
        String token = (String) reg.get("token");
        Assertions.assertNotNull(token);

        // Create doctor directly via repository
        User docUser = new User();
        docUser.setName("Dr Who");
        docUser.setEmail("dr@example.com");
        docUser.setPasswordHash(passwordEncoder.encode("pw"));
        docUser.setRole(com.example.demo.model.Role.DOCTOR);
        userRepository.save(docUser);

        Doctor doctor = new Doctor();
        doctor.setUser(docUser);
        doctor.setSpecialty("General");
        doctor = doctorRepository.save(doctor);

        // find patient profile
        User patientUser = userRepository.findByEmail("patient@example.com").orElseThrow();
        PatientProfile profile = patientProfileRepository.findAll().stream().filter(p -> p.getUser().getId().equals(patientUser.getId())).findFirst().orElseThrow();

        // Create appointment via API
        Appointment ap = new Appointment();
        Appointment doctorRef = new Appointment();
        doctorRef.setId(doctor.getId());
        Appointment patientRef = new Appointment();
        patientRef.setId(profile.getId());
        // Build JSON manually
        Map<String, Object> apBody = Map.of(
                "doctor", Map.of("id", doctor.getId()),
                "patient", Map.of("id", profile.getId()),
                "startTime", LocalDateTime.now().plusDays(1).withSecond(0).withNano(0).toString(),
                "endTime", LocalDateTime.now().plusDays(1).plusHours(1).withSecond(0).withNano(0).toString()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(apBody, headers);

        ResponseEntity<Appointment> apResp = restTemplate.postForEntity(baseUrl("/appointments"), req, Appointment.class);
        Assertions.assertEquals(HttpStatus.OK, apResp.getStatusCode());
        Appointment created = apResp.getBody();
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getId());

        // Check-in
        HttpEntity<Void> checkReq = new HttpEntity<>(headers);
        ResponseEntity<Appointment> checkResp = restTemplate.postForEntity(baseUrl("/appointments/" + created.getId() + "/checkin"), checkReq, Appointment.class);
        Assertions.assertEquals(HttpStatus.OK, checkResp.getStatusCode());
        Appointment checked = checkResp.getBody();
        Assertions.assertNotNull(checked);
        Assertions.assertEquals(AppointmentStatus.CHECKED_IN, checked.getStatus());
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
