package com.example.lab4.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    private static final String ERROR_CODE = "USER_001";
    
    public UserNotFoundException(Long userId) {
        super(
            String.format("User not found with id: %d", userId),
            ERROR_CODE,
            HttpStatus.NOT_FOUND
        );
    }
}
