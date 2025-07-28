package com.example.lab4.service.impl;

import com.example.lab4.repository.DashboardRepository;
import com.example.lab4.repository.DashboardRepository.DashboardStats;
import com.example.lab4.service.AuditService;
import com.example.lab4.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final DashboardRepository dashboardRepository;
    private final AuditService auditService;
    
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStats getDashboardStats() {
        auditService.logDashboardAccess("VIEW_STATS");
        try {
            return dashboardRepository.getDashboardStats();
        } catch (Exception e) {
            auditService.logSecurityEvent("DASHBOARD_ACCESS", "FAILED: " + e.getMessage());
            throw e;
        }
    }
}
