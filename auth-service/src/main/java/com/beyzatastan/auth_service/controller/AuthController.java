package com.beyzatastan.auth_service.controller;

import com.beyzatastan.auth_service.dto.request.*;
import com.beyzatastan.auth_service.dto.response.AuthResponse;
import com.beyzatastan.auth_service.dto.response.MessageResponse;
import com.beyzatastan.auth_service.dto.response.UserResponse;
import com.beyzatastan.auth_service.entity.User;
import com.beyzatastan.auth_service.repository.UserRepository;
import com.beyzatastan.auth_service.service.AuthService;
import com.beyzatastan.auth_service.service.EmailProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProducer emailProducer;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {

        String result = authService.signup(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        String[] tokens = result.split("\\|");

        AuthResponse response = AuthResponse.builder()
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(UserResponse.builder()
                        .id(Long.parseLong(tokens[2]))
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .role("EMPLOYEE")
                        .isActive(true)
                        .build())
                .build();

        log.info("User registered successfully: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest request) {

        String result = authService.signin(
                request.getUsernameOrEmail(),
                request.getPassword()
        );

        String[] parts = result.split("\\|");

        AuthResponse response = AuthResponse.builder()
                .accessToken(parts[0])
                .refreshToken(parts[1])
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(UserResponse.builder()
                        .id(Long.parseLong(parts[2]))
                        .username(parts[3])
                        .email(parts[4])
                        .role(parts[5])
                        .isActive(true)
                        .build())
                .build();

        log.info("User signed in successfully: {}", request.getUsernameOrEmail());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());

        AuthResponse response = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();

        log.info("Access token refreshed successfully");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {

        authService.requestPasswordReset(request.getEmail());

        MessageResponse response = MessageResponse.success(
                "Password reset email has been sent. Please check your email."
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {

        authService.resetPassword(request.getResetToken(), request.getNewPassword());

        MessageResponse response = MessageResponse.success(
                "Password has been reset successfully. You can now login with your new password."
        );

        return ResponseEntity.ok(response);
    }

    //employee servisten gelen istek
    @PostMapping("/create-user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user from Employee Service: {}", request.getUsername());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // Welcome email gönder
        try {
            emailProducer.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUsername());
            log.info("Welcome email queued for: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to queue welcome email: {}", e.getMessage());
        }

        UserResponse response = UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .createdAt(savedUser.getCreatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth Service is working!");
    }
}