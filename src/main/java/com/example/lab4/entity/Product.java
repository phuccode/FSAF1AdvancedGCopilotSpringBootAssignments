package com.example.lab4.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Column(name = "average_rating")
    private Double averageRating = 0.0;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @OneToMany(mappedBy = "product")
    private Set<Review> reviews = new HashSet<>();
    
    public void updateAverageRating() {
        if (reviews.isEmpty()) {
            this.averageRating = 0.0;
            this.totalReviews = 0;
        } else {
            double sum = reviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();
            this.totalReviews = reviews.size();
            this.averageRating = sum / this.totalReviews;
        }
    }
}
