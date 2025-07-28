package com.example.lab4.config;

import com.example.lab4.exception.BusinessException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {
    
    private final LoadingCache<String, Bucket> buckets;
    
    public RateLimitConfig() {
        buckets = CacheBuilder.newBuilder()
            .maximumSize(10000) // Maximum number of cached rate limiters
            .expireAfterAccess(1, TimeUnit.HOURS) // Clean up unused entries
            .build(new CacheLoader<>() {
                @Override
                public Bucket load(String key) {
                    return createBucket(key.startsWith("AUTH_"));
                }
            });
    }

    public LoadingCache<String, Bucket> getBuckets() {
        return buckets;
    }
    
    private Bucket createBucket(boolean authenticated) {
        if (authenticated) {
            // Authenticated users get more generous limits
            return Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
                .build();
        } else {
            // Unauthenticated requests get stricter limits
            return Bucket.builder()
                .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1))))
                .build();
        }
    }
    
    public static class RateLimitExceededException extends BusinessException {
        private static final String ERROR_CODE = "RATE_001";
        
        public RateLimitExceededException(String message) {
            super(message, ERROR_CODE, HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
