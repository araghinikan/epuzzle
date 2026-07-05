package com.nikan.epuzzle.service;

import com.nikan.epuzzle.dto.*;
import com.nikan.epuzzle.exception.CustomException;
import com.nikan.epuzzle.exception.MessageCode;
import com.nikan.epuzzle.model.ApplicationUser;
import com.nikan.epuzzle.repository.ApplicationUserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final MailService mailService;
    private final MessageSource messageSource;
    private final Random random = new Random();

    public AuthService(ApplicationUserRepository userRepository, PasswordEncoder passwordEncoder,
                       JWTService jwtService, MailService mailService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
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

    private String generateVerificationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(MessageCode.EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(MessageCode.USERNAME_ALREADY_TAKEN);
        }

        String verificationCode = generateVerificationCode();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        ApplicationUser user = new ApplicationUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEmailVerified(false);
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiry(expiryTime);

        userRepository.save(user);
        mailService.sendSignupVerification(user.getEmail(), verificationCode);

        return new AuthResponse(getMessage(MessageCode.SIGNUP_SUCCESS));
    }

    public AuthResponse verifyEmail(VerifyCodeRequest request) {
        ApplicationUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(MessageCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new CustomException(MessageCode.EMAIL_ALREADY_VERIFIED);
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            throw new CustomException(MessageCode.INVALID_OR_EXPIRED_CODE);
        }

        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new CustomException(MessageCode.INVALID_OR_EXPIRED_CODE);
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

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

    public AuthResponse login(LoginRequest request) {
        Optional<ApplicationUser> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new CustomException(MessageCode.INVALID_EMAIL_OR_PASSWORD);
        }

        ApplicationUser user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(MessageCode.INVALID_EMAIL_OR_PASSWORD);
        }

        if (!user.isEmailVerified()) {
            String newCode = generateVerificationCode();
            user.setVerificationCode(newCode);
            user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);

            mailService.sendSignupVerification(user.getEmail(), newCode);
            throw new CustomException(MessageCode.EMAIL_NOT_VERIFIED);
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

    public AuthResponse resendVerificationCode(VerifyEmailRequest request) {
        ApplicationUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(MessageCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new CustomException(MessageCode.EMAIL_ALREADY_VERIFIED);
        }

        String newCode = generateVerificationCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        mailService.sendSignupVerification(user.getEmail(), newCode);

        return new AuthResponse(getMessage(MessageCode.RESEND_CODE_SUCCESS));
    }

    public ResetTokenResponse resetPassword(ResetPasswordRequest request) {
        Optional<ApplicationUser> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new CustomException(MessageCode.EMAIL_NOT_FOUND);
        }

        ApplicationUser user = userOptional.get();

        if (!user.isEmailVerified()) {
            throw new CustomException(MessageCode.EMAIL_NOT_VERIFIED_FOR_RESET);
        }

        String resetCode = generateVerificationCode();
        user.setVerificationCode(resetCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        mailService.sendResetPasswordVerification(user.getEmail(), resetCode);

        return new ResetTokenResponse(getMessage(MessageCode.RESET_CODE_SENT), null);
    }

    @Transactional
    public AuthResponse confirmResetPassword(ResetPasswordConfirmRequest request) {
        ApplicationUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(MessageCode.USER_NOT_FOUND));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            throw new CustomException(MessageCode.INVALID_OR_EXPIRED_CODE);
        }

        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new CustomException(MessageCode.INVALID_OR_EXPIRED_CODE);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return new AuthResponse(getMessage(MessageCode.PASSWORD_RESET_SUCCESS));
    }

    public AuthResponse changePassword(ChangePasswordRequest request, String username) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(MessageCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(MessageCode.CURRENT_PASSWORD_INCORRECT);
        }

        String changeCode = generateVerificationCode();
        user.setVerificationCode(changeCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        mailService.sendChangePasswordVerification(user.getEmail(), changeCode);

        return new AuthResponse(getMessage(MessageCode.PASSWORD_CHANGE_CODE_SENT));
    }

    public AuthResponse confirmChangePassword(ChangePasswordConfirmRequest request, String username) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(MessageCode.USER_NOT_FOUND));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            throw new CustomException(MessageCode.INVALID_OR_EXPIRED_CODE);
        }

        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new CustomException(MessageCode.INVALID_OR_EXPIRED_CODE);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return new AuthResponse(getMessage(MessageCode.PASSWORD_CHANGE_SUCCESS));
    }
}