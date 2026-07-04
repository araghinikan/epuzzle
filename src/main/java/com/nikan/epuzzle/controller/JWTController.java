package com.nikan.epuzzle.controller;

import com.nikan.epuzzle.service.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jwt")
public class JWTController {

    private final JWTService jwtService;

    public JWTController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/create/{username}")
    public ResponseEntity<String> createJWT(@PathVariable String username , @RequestBody String email , @PathVariable String role ) {
        String jwt = jwtService.generateToken(username , email , role);
        return ResponseEntity.ok(jwt);
    }
}

