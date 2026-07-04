package com.nikan.epuzzle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    @GetMapping("/confirm-reset")
    public String confirmResetPage() {
        return "confirm-reset";
    }

    @GetMapping("/verify-email")
    public String verifyEmailPage() {
        return "verify-email";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}