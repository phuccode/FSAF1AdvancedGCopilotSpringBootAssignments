package com.example.lab4.config;

import org.springframework.stereotype.Component;

@Component
public class MaxMemoryHealthIndicator {

    private static final double MEMORY_THRESHOLD = 0.9; // 90% threshold

    /**
     * This class will be enhanced to implement HealthIndicator once dependencies are resolved.
     * For now, it provides basic memory monitoring functionality.
     */
    
    public String getMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsageRatio = (double) usedMemory / maxMemory;
        double memoryUsagePercentage = memoryUsageRatio * 100;
        
        String status = memoryUsageRatio > MEMORY_THRESHOLD ? "DOWN" : "UP";
        
        return String.format(
            "Memory Status: %s | Usage: %.2f%% | Used: %s | Max: %s | Threshold: %.0f%%",
            status, 
            memoryUsagePercentage,
            formatBytes(usedMemory),
            formatBytes(maxMemory),
            MEMORY_THRESHOLD * 100
        );
    }
    
    public boolean isMemoryHealthy() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsageRatio = (double) usedMemory / maxMemory;
        return memoryUsageRatio <= MEMORY_THRESHOLD;
    }
    
    public double getMemoryUsagePercentage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        return ((double) usedMemory / maxMemory) * 100;
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
