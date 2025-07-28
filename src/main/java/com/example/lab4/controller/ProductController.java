package com.example.lab4.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> product) {
        // Simplified response for testing
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        // Simplified response for testing
        return ResponseEntity.ok(List.of(
            Map.of("id", 1, "name", "Test Product", "price", 99.99)
        ));
    }
}
