package com.example.lab4.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception class for business logic exceptions.
 * Provides a standard way to handle business-related errors with HTTP status codes.
 */
public abstract class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    
    protected BusinessException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
    
    protected BusinessException(String message, String code, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getCode() {
        return code;
    }
}
