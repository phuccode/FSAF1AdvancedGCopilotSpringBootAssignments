package com.example.lab4.service;

import com.example.lab4.entity.Review;
import com.example.lab4.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface ReviewService {
    public record ReviewRequest(
        @NotBlank Long productId,
        @Min(1) @Max(5) Integer rating,
        @NotBlank @Size(min = 10, max = 1000) String comment
    ) {}

    Review createReview(ReviewRequest request, User currentUser);
}
