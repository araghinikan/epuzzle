package com.nikan.epuzzle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailSenderService {
    private final JavaMailSender mailSender;
    private final String email;

    public MailSenderService(JavaMailSender mailSender, @Value("${spring.mail.username}") String email) {
        this.mailSender = mailSender;
        this.email = email;
    }

    public void sendMail(String to, String text, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(email);
        mailSender.send(message);
    }

    public void sendHtmlMail(String to, String htmlContent, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(email);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
}