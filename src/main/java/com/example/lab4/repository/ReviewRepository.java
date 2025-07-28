package com.example.lab4.repository;

import com.example.lab4.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    @Query("SELECT COUNT(r), COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = :productId")
    Object[] getProductRatingStats(@Param("productId") Long productId);
    
    @Query(value = """
            SELECT 
                COALESCE(SUM(p.price), 0) as totalRevenue,
                COUNT(DISTINCT o.id) as totalOrders,
                COUNT(DISTINCT CASE 
                    WHEN YEAR(u.created_date) = YEAR(CURRENT_DATE) 
                    AND MONTH(u.created_date) = MONTH(CURRENT_DATE) 
                    THEN u.id 
                    END) as newCustomersThisMonth
            FROM orders o
            LEFT JOIN order_products op ON o.id = op.order_id
            LEFT JOIN products p ON op.product_id = p.id
            LEFT JOIN users u ON o.user_id = u.id
            WHERE o.status = 'DELIVERED'
            """, nativeQuery = true)
    DashboardStats getDashboardStats();
    
    interface DashboardStats {
        BigDecimal getTotalRevenue();
        Long getTotalOrders();
        Long getNewCustomersThisMonth();
    }
}
