package com.example.lab4.dto;

import jakarta.validation.constraints.*;

public record UserRegistrationRequest(
    @NotNull(message = "Email is required")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
           message = "Please provide a valid email address")
    String email,

    @NotNull(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "Password must be at least 8 characters long and contain at least one digit, " +
                 "one lowercase, one uppercase letter and one special character"
    )
    String password,

    @NotNull(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(
        regexp = "^[a-zA-Z\\s]+$",
        message = "Name can only contain letters and spaces"
    )
    String name
) {
    // Custom validation method
    public UserRegistrationRequest {
        // Trim strings to prevent whitespace-only inputs
        email = email != null ? email.trim().toLowerCase() : null;
        name = name != null ? name.trim() : null;
        
        // Additional validation can be added here
        if (email != null && email.length() > 100) {
            throw new IllegalArgumentException("Email address is too long");
        }
        
        if (password != null && password.length() > 72) { // BCrypt max length
            throw new IllegalArgumentException("Password is too long");
        }
    }
}
