package com.nikan.epuzzle.repository;

import com.nikan.epuzzle.model.Mail;
import com.nikan.epuzzle.model.MailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<Mail, Integer> {
    Mail findByEmailAndCodeAndMailType(String email, String code, MailType mailType);
    List<Mail> findByCreatedAtBefore(LocalDateTime dateTime);
}