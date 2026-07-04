package com.nikan.epuzzle.repository;

import com.nikan.epuzzle.model.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Integer> {

    Mail findByEmailAndCreatedAtLessThan(String email, LocalDateTime createdAt);

    Mail findByEmailAndCode(String email, String code);
}
