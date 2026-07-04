package com.nikan.epuzzle.controller;

import com.nikan.epuzzle.service.MailSenderService;
import com.nikan.epuzzle.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
public class MailSenderController {

    private final MailSenderService mailSenderService;
    private final MailService mailService;

    public MailSenderController(MailSenderService mailSenderService,  MailService mailService) {
        this.mailSenderService = mailSenderService;
        this.mailService = mailService;
    }

    @GetMapping("/send/{email}")
    public ResponseEntity<String> send(@PathVariable String email) {
        mailService.login(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/approve/{code}/{email}")
    public ResponseEntity<?> approve(@PathVariable String email , @PathVariable String code) {
        boolean approve = mailService.approve(email , code);
        return ResponseEntity.ok(approve);
    }

}
