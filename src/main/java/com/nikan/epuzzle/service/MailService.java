package com.nikan.epuzzle.service;

import com.nikan.epuzzle.model.ApplicationUser;
import com.nikan.epuzzle.model.Mail;
import com.nikan.epuzzle.repository.ApplicationUserRepository;
import com.nikan.epuzzle.repository.MailRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class MailService {
    private final MailRepository mailRepository;
    private final MailSenderService mailSenderService;

    public MailService(MailRepository mailRepository, MailSenderService mailSender, MailSenderService mailSenderService) {
        this.mailRepository = mailRepository;
        this.mailSenderService = mailSenderService;
    }

    public void login(String email) {
        Mail byMailAndCreatedAtLessThan = mailRepository.findByEmailAndCreatedAtLessThan(email, LocalDateTime.now().minusMinutes(3));

        if (byMailAndCreatedAtLessThan == null) {

            Random random = new Random();
            int code = random.nextInt(1000, 9999);
            Mail mail = new Mail();
            mail.setCode(String.valueOf(code));
            mail.setEmail(email);

            mailRepository.save(mail);
            mailSenderService.sendMail(email, "code is" + code, "Code");

        }

    }

    public boolean approve(String email, String code) {
        Mail mail = mailRepository.findByEmailAndCode(email, code);

        if (mail == null){
            return false;
        }else{
            return true;
        }

    }
}