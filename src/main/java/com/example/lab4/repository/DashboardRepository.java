package com.example.lab4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import com.example.lab4.entity.Order;

public interface DashboardRepository extends JpaRepository<Order, Long> {
    
    @Query(value = """
            SELECT 
                COALESCE(SUM(p.price), 0) as totalRevenue,
                COUNT(DISTINCT o.id) as totalOrders,
                COUNT(DISTINCT CASE 
                    WHEN DATE_FORMAT(u.created_date, '%Y-%m') = DATE_FORMAT(CURRENT_TIMESTAMP, '%Y-%m')
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
