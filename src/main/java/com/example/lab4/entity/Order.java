package com.example.lab4.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();
    
    private LocalDateTime orderDate = LocalDateTime.now();
    
    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        recalculateTotal();
    }
    
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        recalculateTotal();
    }
    
    private void recalculateTotal() {
        this.totalAmount = orderItems.stream()
            .map(OrderItem::getSubTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<Product> getProducts() {
        return orderItems.stream()
            .map(OrderItem::getProduct)
            .collect(Collectors.toList());
    }
    
    public enum OrderStatus {
        PENDING, PROCESSING, DELIVERED, CANCELLED
    }

}
