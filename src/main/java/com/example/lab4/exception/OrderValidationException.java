package com.example.lab4.exception;

import org.springframework.http.HttpStatus;

public class OrderValidationException extends BusinessException {
    private static final String ERROR_CODE = "ORDER_001";
    
    public OrderValidationException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }
    
    public OrderValidationException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, cause);
    }
}
