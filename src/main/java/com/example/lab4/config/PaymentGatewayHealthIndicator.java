package com.example.lab4.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component("paymentGateway")
public class PaymentGatewayHealthIndicator {
    
    private final Random random = new Random();

    /**
     * Simulates checking payment gateway status
     * In a real application, this would ping the actual payment provider's status endpoint
     */
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        boolean isUp = random.nextBoolean();
        
        health.put("status", isUp ? "UP" : "DOWN");
        health.put("details", Map.of(
            "provider", "MockPaymentProvider",
            "description", isUp ? "Payment gateway is operational" : "Payment gateway is experiencing issues",
            "error", isUp ? null : "Connection timeout or service unavailable",
            "lastChecked", System.currentTimeMillis()
        ));
        
        return health;
    }
    
    public boolean isHealthy() {
        // In real implementation, this would check actual payment gateway status
        return random.nextBoolean();
    }
    
    public String getStatus() {
        return isHealthy() ? "UP" : "DOWN";
    }
}
