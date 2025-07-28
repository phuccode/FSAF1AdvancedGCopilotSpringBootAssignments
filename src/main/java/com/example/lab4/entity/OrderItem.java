package com.example.lab4.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    
    private BigDecimal priceAtOrder; // Lưu giá tại thời điểm đặt hàng

    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.priceAtOrder = product.getPrice();
    }

    public BigDecimal getSubTotal() {
        return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
    }
}
