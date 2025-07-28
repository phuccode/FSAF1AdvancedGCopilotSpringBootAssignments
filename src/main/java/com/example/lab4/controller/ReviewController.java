package com.example.lab4.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllReviews() {
        // Mock data for testing
        List<Map<String, Object>> reviews = Arrays.asList(
            Map.of(
                "id", 1,
                "productId", 1,
                "rating", 5,
                "comment", "Great product!"
            )
        );
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody Map<String, Object> review) {
        // For testing purposes, just echo back the review
        return ResponseEntity.ok(review);
    }
}
