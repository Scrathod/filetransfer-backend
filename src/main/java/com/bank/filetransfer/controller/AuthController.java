package com.bank.filetransfer.controller;

import com.bank.filetransfer.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;


    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestParam String username,
                                   @RequestParam String password) {


// Demo only — replace with LDAP / DB validation
        if (!"admin".equals(username) || !"admin123".equals(password)) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }


        String token = jwtService.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
