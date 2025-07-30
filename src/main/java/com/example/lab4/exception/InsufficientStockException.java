package com.example.lab4.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BusinessException {
    private static final String ERROR_CODE = "PRODUCT_003";
    
    public InsufficientStockException(String message) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT);
    }
}
