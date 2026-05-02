package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.JwtUtil;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password are required"));
        }

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
                    }
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "role", user.getRole().name(),
                            "email", user.getEmail(),
                            "id", user.getId()
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "invalid credentials")));
    }
}
