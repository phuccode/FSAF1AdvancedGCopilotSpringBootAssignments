package com.example.lab4.service;

import com.example.lab4.repository.DashboardRepository.DashboardStats;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

public interface DashboardService {
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    DashboardStats getDashboardStats();
}
