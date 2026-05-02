package com.example.demo.integration;

import com.example.demo.model.PatientProfile;
import com.example.demo.model.User;
import com.example.demo.repository.PatientProfileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PatientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PatientServiceTests {
    @Autowired
    private PatientService patientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Test
    void findByUserUsesRepositoryQuery() {
        User u = new User();
        u.setName("PUser");
        u.setEmail("puser@example.com");
        u.setPasswordHash("x");
        userRepository.save(u);

        PatientProfile p = new PatientProfile();
        p.setUser(u);
        patientProfileRepository.save(p);

        var opt = patientService.findByUser(u);
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals(u.getId(), opt.get().getUser().getId());
    }
}
