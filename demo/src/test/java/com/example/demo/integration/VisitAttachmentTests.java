package com.example.demo.integration;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.PatientProfile;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.VisitRecord;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientProfileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VisitRecordRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VisitAttachmentTests {
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

    @Autowired
    private VisitRecordRepository visitRecordRepository;

    @Test
    void doctorCanCreateVisitWithAttachments() {
        // register patient
        Map<String, String> body = Map.of("name", "VPatient", "email", "vpatient@example.com", "phone", "000", "password", "secret");
        ResponseEntity<Map> regResp = restTemplate.postForEntity(baseUrl("/auth/register"), body, Map.class);
        Assertions.assertEquals(HttpStatus.OK, regResp.getStatusCode());
        String patientToken = (String) regResp.getBody().get("token");

        // create doctor user
        User docUser = new User();
        docUser.setName("Dr Test");
        docUser.setEmail("dr2@example.com");
        docUser.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("pw"));
        docUser.setRole(Role.DOCTOR);
        userRepository.save(docUser);

        Doctor doctor = new Doctor();
        doctor.setUser(docUser);
        doctor = doctorRepository.save(doctor);

        // find patient profile
        User patientUser = userRepository.findByEmail("vpatient@example.com").orElseThrow();
        PatientProfile profile = patientProfileRepository.findAll().stream().filter(p -> p.getUser().getId().equals(patientUser.getId())).findFirst().orElseThrow();

        // create appointment as patient
        Map<String, Object> apBody = Map.of(
                "doctor", Map.of("id", doctor.getId()),
                "patient", Map.of("id", profile.getId()),
                "startTime", LocalDateTime.now().plusDays(1).withSecond(0).withNano(0).toString(),
                "endTime", LocalDateTime.now().plusDays(1).plusHours(1).withSecond(0).withNano(0).toString()
        );
        HttpHeaders ph = new HttpHeaders();
        ph.setBearerAuth(patientToken);
        ph.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> preq = new HttpEntity<>(apBody, ph);
        ResponseEntity<Appointment> apResp = restTemplate.postForEntity(baseUrl("/appointments"), preq, Appointment.class);
        Assertions.assertEquals(HttpStatus.OK, apResp.getStatusCode());
        Appointment created = apResp.getBody();

        // check-in as patient
        HttpHeaders ch = new HttpHeaders();
        ch.setBearerAuth(patientToken);
        HttpEntity<Void> checkReq = new HttpEntity<>(ch);
        ResponseEntity<Appointment> checkResp = restTemplate.postForEntity(baseUrl("/appointments/" + created.getId() + "/checkin"), checkReq, Appointment.class);
        Assertions.assertEquals(HttpStatus.OK, checkResp.getStatusCode());

        // login as doctor to create visit
        Map<String, String> login = Map.of("email", "dr2@example.com", "password", "pw");
        ResponseEntity<Map> loginResp = restTemplate.postForEntity(baseUrl("/auth/login"), login, Map.class);
        Assertions.assertEquals(HttpStatus.OK, loginResp.getStatusCode());
        String doctorToken = (String) loginResp.getBody().get("token");

        // create visit record with attachments
        Map<String, Object> visitBody = Map.of(
                "appointment", Map.of("id", created.getId()),
                "notes", "Visit notes",
                "procedures", "Procedure X",
                "cost", 123.45,
                "attachments", List.of(Map.of("url", "http://example.com/file.pdf", "type", "application/pdf", "size", 1024))
        );

        HttpHeaders dh = new HttpHeaders();
        dh.setBearerAuth(doctorToken);
        dh.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> dreq = new HttpEntity<>(visitBody, dh);
        ResponseEntity<Map> visitResp = restTemplate.postForEntity(baseUrl("/visits"), dreq, Map.class);
        Assertions.assertEquals(HttpStatus.OK, visitResp.getStatusCode());
        Map respBody = visitResp.getBody();
        Assertions.assertNotNull(respBody.get("id"));

        // verify attachments persisted
        Number vid = (Number) respBody.get("id");
        VisitRecord vr = visitRecordRepository.findById(vid.longValue()).orElseThrow();
        Assertions.assertNotNull(vr.getAttachments());
        Assertions.assertFalse(vr.getAttachments().isEmpty());
        Assertions.assertEquals("application/pdf", vr.getAttachments().get(0).getType());
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
