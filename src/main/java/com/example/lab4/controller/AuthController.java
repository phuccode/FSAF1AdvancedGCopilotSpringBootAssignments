package com.example.lab4.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        // Mock login functionality for demonstration
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // Simple validation for demo
        if ("admin".equals(username) && "password".equals(password)) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "token", "mock-jwt-token-" + System.currentTimeMillis()
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Invalid credentials"
            ));
        }
    }
}
