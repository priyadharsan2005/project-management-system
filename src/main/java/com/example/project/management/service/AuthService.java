
package com.example.project.management.service;

import com.example.project.management.entity.User;
import com.example.project.management.repository.UserRepository;
import com.example.project.management.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        // User not found
        if (user == null) {
            return null;
        }

        // Check password using BCrypt
        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            return null;
        }

        // Generate JWT token
        return jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}

