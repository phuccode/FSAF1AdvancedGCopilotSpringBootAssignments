package com.example.lab4.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardStats() {
        // Simplified stats for testing
        return ResponseEntity.ok(Map.of(
            "totalUsers", 100,
            "totalOrders", 50,
            "totalRevenue", 10000
        ));
    }
}
