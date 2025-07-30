package com.example.lab4.controller;

import com.example.lab4.config.MaxMemoryHealthIndicator;
import com.example.lab4.config.PaymentGatewayHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private MaxMemoryHealthIndicator maxMemoryHealthIndicator;

    @Autowired
    private PaymentGatewayHealthIndicator paymentGatewayHealthIndicator;

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getHealthInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "Health endpoints are available at /actuator/health");
        info.put("custom-memory-check", "Available at /api/health/memory");
        info.put("custom-payment-check", "Available at /api/health/payment");
        info.put("custom-full-check", "Available at /api/health/full");
        info.put("note", "Access /actuator/health for full system health details");
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/memory")
    public ResponseEntity<Map<String, String>> getMemoryStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("memoryHealth", maxMemoryHealthIndicator.getMemoryStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment")
    public ResponseEntity<Map<String, Object>> getPaymentGatewayStatus() {
        return ResponseEntity.ok(paymentGatewayHealthIndicator.getHealth());
    }

    @GetMapping("/full")
    public ResponseEntity<Map<String, Object>> getFullHealthStatus() {
        Map<String, Object> fullHealth = new HashMap<>();
        
        // Overall status
        boolean memoryHealthy = maxMemoryHealthIndicator.isMemoryHealthy();
        boolean paymentHealthy = paymentGatewayHealthIndicator.isHealthy();
        String overallStatus = (memoryHealthy && paymentHealthy) ? "UP" : "DOWN";
        
        fullHealth.put("status", overallStatus);
        fullHealth.put("components", Map.of(
            "memory", Map.of(
                "status", memoryHealthy ? "UP" : "DOWN",
                "details", maxMemoryHealthIndicator.getMemoryStatus()
            ),
            "paymentGateway", paymentGatewayHealthIndicator.getHealth()
        ));
        
        return ResponseEntity.ok(fullHealth);
    }
}
