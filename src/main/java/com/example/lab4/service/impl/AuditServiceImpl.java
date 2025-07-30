package com.example.lab4.service.impl;

import com.example.lab4.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    
    @Override
    public void logDashboardAccess(String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        log.info("Dashboard access - Action: {}, User: {}, Time: {}", 
            action, username, LocalDateTime.now());
    }
    
    @Override
    public void logReviewAction(String action, Long productId, Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        log.info("Review action - Action: {}, Product: {}, User: {}, Actor: {}, Time: {}", 
            action, productId, userId, username, LocalDateTime.now());
    }
    
    @Override
    public void logSecurityEvent(String event, String outcome) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        log.warn("Security event - Event: {}, Outcome: {}, User: {}, Time: {}", 
            event, outcome, username, LocalDateTime.now());
    }
}
