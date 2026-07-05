package com.nikan.epuzzle.controller;

import com.nikan.epuzzle.dto.*;
import com.nikan.epuzzle.exception.CustomException;
import com.nikan.epuzzle.exception.MessageCode;
import com.nikan.epuzzle.service.AuthService;
import com.nikan.epuzzle.service.JWTService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;
    private final MessageSource messageSource;

    public AuthController(AuthService authService, JWTService jwtService, MessageSource messageSource) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.messageSource = messageSource;
    }

    private String getMessage(MessageCode messageCode) {
        return messageSource.getMessage(
                messageCode.name(),
                null,
                messageCode.name(),
                LocaleContextHolder.getLocale()
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyCodeRequest request) {
        AuthResponse response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetTokenResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm-reset")
    public ResponseEntity<?> confirmReset(@Valid @RequestBody ResetPasswordConfirmRequest request) {
        AuthResponse response = authService.confirmResetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(MessageCode.INVALID_AUTHORIZATION);
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        AuthResponse response = authService.changePassword(request, username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm-change-password")
    public ResponseEntity<?> confirmChangePassword(
            @Valid @RequestBody ChangePasswordConfirmRequest request,
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(MessageCode.INVALID_AUTHORIZATION);
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        AuthResponse response = authService.confirmChangePassword(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(MessageCode.INVALID_AUTHORIZATION);
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtService.validatePasswordResetToken(token);

        if (isValid) {
            return ResponseEntity.ok(new AuthResponse(getMessage(MessageCode.TOKEN_VALID)));
        } else {
            throw new CustomException(MessageCode.INVALID_TOKEN);
        }
    }
}