package com.example.lab4.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

import java.util.Map;

public class RateLimitInterceptor implements HandlerInterceptor {
    private final Map<String, Bucket> buckets;

    public RateLimitInterceptor(Map<String, Bucket> buckets) {
        this.buckets = buckets;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return false;
        }

        Bucket bucket = buckets.computeIfAbsent(apiKey, k -> createNewBucket());

        if (!bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }

        return true;
    }

    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }
}
