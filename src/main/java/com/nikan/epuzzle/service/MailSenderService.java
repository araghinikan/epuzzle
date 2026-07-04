package com.nikan.epuzzle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
}