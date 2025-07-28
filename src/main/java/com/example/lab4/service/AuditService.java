package com.example.lab4.service;

public interface AuditService {
    void logDashboardAccess(String action);
    void logReviewAction(String action, Long productId, Long userId);
    void logSecurityEvent(String event, String outcome);
}
