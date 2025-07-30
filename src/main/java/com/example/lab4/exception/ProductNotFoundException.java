package com.example.lab4.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BusinessException {
    private static final String ERROR_CODE = "PRODUCT_001";
    
    public ProductNotFoundException(Long id) {
        super(
            String.format("Product not found with id: %d", id),
            ERROR_CODE,
            HttpStatus.NOT_FOUND
        );
    }
    
    public ProductNotFoundException(String message) {
        super(message, ERROR_CODE, HttpStatus.NOT_FOUND);
    }
}
