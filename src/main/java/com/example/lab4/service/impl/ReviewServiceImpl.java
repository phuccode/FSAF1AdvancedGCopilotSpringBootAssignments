package com.example.lab4.service.impl;

import com.example.lab4.entity.Product;
import com.example.lab4.entity.Review;
import com.example.lab4.entity.User;
import com.example.lab4.exception.ProductNotFoundException;
import com.example.lab4.exception.ReviewValidationException;
import com.example.lab4.repository.ProductRepository;
import com.example.lab4.repository.ReviewRepository;
import com.example.lab4.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Review createReview(ReviewRequest request, User currentUser) {
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new ProductNotFoundException(request.productId()));
            
        // Check if user has purchased the product
        boolean hasPurchased = currentUser.getOrders().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(orderItem -> orderItem.getProduct())
                .anyMatch(p -> p.getId().equals(product.getId()));
        
        if (!hasPurchased) {
            throw new ReviewValidationException("You must purchase the product before reviewing it");
        }
        
        // Check if user has already reviewed this product
        boolean hasReviewed = reviewRepository.existsByUserIdAndProductId(currentUser.getId(), product.getId());
        if (hasReviewed) {
            throw new ReviewValidationException("You have already reviewed this product");
        }
        
        Review review = new Review();
        review.setUser(currentUser);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setComment(request.comment());
        
        review = reviewRepository.save(review);
        
        // Update product's average rating using the database query
        updateProductAverageRating(product.getId());
        
        return review;
    }
    
    private void updateProductAverageRating(Long productId) {
        Object[] stats = reviewRepository.getProductRatingStats(productId);
        Long totalReviews = (Long) stats[0];
        Double averageRating = (Double) stats[1];
        
        // Get and update the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setTotalReviews(totalReviews.intValue());
        product.setAverageRating(averageRating);
        productRepository.save(product);
    }
}
