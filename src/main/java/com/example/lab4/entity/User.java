package com.example.lab4.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    private String password;
    
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new HashSet<>();
    
    @OneToMany(mappedBy = "user")
    private Set<Review> reviews = new HashSet<>();
}
