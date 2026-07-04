package com.nikan.epuzzle.service;

import com.nikan.epuzzle.dto.*;
import com.nikan.epuzzle.model.ApplicationUser;
import com.nikan.epuzzle.repository.ApplicationUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private ApplicationUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JWTService jwtService;

    public AuthService(ApplicationUserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        ApplicationUser user = new ApplicationUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        ApplicationUser savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Optional<ApplicationUser> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        ApplicationUser user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public ResetTokenResponse resetPassword(ResetPasswordRequest request) {
        Optional<ApplicationUser> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email not found");
        }

        ApplicationUser user = userOptional.get();
        String resetToken = jwtService.generatePasswordResetToken(user.getEmail());

        return new ResetTokenResponse(
                "Password reset link sent to your email",
                resetToken
        );
    }

    @Transactional
    public AuthResponse confirmResetPassword(ConfirmResetRequest request) {
        if (!jwtService.validatePasswordResetToken(request.getResetToken())) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        String email = jwtService.getEmailFromResetToken(request.getResetToken());

        Optional<ApplicationUser> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        ApplicationUser user = userOptional.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new AuthResponse("Password has been reset successfully");
    }

    public AuthResponse changePassword(ChangePasswordRequest request, String username) {
        Optional<ApplicationUser> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        ApplicationUser user = userOptional.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new AuthResponse("Password changed successfully");
    }
}