package com.example.lab4.service;

import com.example.lab4.entity.Order;
import com.example.lab4.entity.Product;
import com.example.lab4.entity.User;
import com.example.lab4.repository.ProductRepository;
import com.example.lab4.repository.UserRepository;
import com.example.lab4.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should create order with multiple products successfully")
    void shouldCreateOrderWithMultipleProducts() {
        // Arrange - Refactored using helper methods
        TestData testData = setupInitialData();
        
        Map<Long, Integer> productQuantities = Map.of(
            testData.products.get(0).getId(), 1,
            testData.products.get(1).getId(), 2,
            testData.products.get(2).getId(), 1
        );

        OrderServiceImpl.OrderRequest orderRequest = new OrderServiceImpl.OrderRequest(
            testData.user.getId(),
            productQuantities
        );

        // Act
        Order createdOrder = orderService.placeOrder(orderRequest);

        // Assert
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getUser().getId()).isEqualTo(testData.user.getId());
        assertThat(createdOrder.getOrderItems()).hasSize(3);
        
        // Verify total amount calculation
        BigDecimal expectedTotal = new BigDecimal("999.99")
            .add(new BigDecimal("29.99").multiply(new BigDecimal("2")))
            .add(new BigDecimal("149.99"));
        assertThat(createdOrder.getTotalAmount()).isEqualByComparingTo(expectedTotal);

        // Verify stock updates
        verifyStockUpdates(testData.products, List.of(9, 48, 24));
    }

    @Test
    @DisplayName("Should create order with single product successfully")
    void shouldCreateOrderWithSingleProduct() {
        // Arrange - Refactored using helper method
        String uniqueId = String.valueOf(System.currentTimeMillis());
        User testUser = createTestUser("singleuser" + uniqueId, "singleuser" + uniqueId + "@example.com");
        Product product = createTestProduct("Smartphone" + uniqueId, "Latest model smartphone", 
            new BigDecimal("799.99"), 15, 4.6, 300);

        Map<Long, Integer> productQuantities = Map.of(product.getId(), 1);
        OrderServiceImpl.OrderRequest orderRequest = new OrderServiceImpl.OrderRequest(
            testUser.getId(),
            productQuantities
        );

        // Act
        Order createdOrder = orderService.placeOrder(orderRequest);

        // Assert
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(createdOrder.getOrderItems()).hasSize(1);
        assertThat(createdOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("799.99"));

        // Verify stock update
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(14);
    }

    // Helper Methods - Extracted from long Arrange blocks

    /**
     * Creates initial test data with a user and multiple products.
     * This helper method extracts the repetitive setup code from test methods.
     */
    private TestData setupInitialData() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        User user = createTestUser("testuser" + uniqueId, "testuser" + uniqueId + "@example.com");
        
        List<Product> products = List.of(
            createTestProduct("Laptop" + uniqueId, "High-performance laptop", 
                new BigDecimal("999.99"), 10, 4.5, 100),
            createTestProduct("Mouse" + uniqueId, "Wireless optical mouse", 
                new BigDecimal("29.99"), 50, 4.2, 75),
            createTestProduct("Keyboard" + uniqueId, "Mechanical gaming keyboard", 
                new BigDecimal("149.99"), 25, 4.8, 200)
        );
        
        return new TestData(user, products);
    }

    /**
     * Creates a test user with the specified username and email.
     * Encapsulates user creation logic to reduce duplication.
     */
    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hashedpassword123");
        user.setCreatedDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Creates a test product with the specified properties.
     * Encapsulates product creation logic to reduce duplication.
     */
    private Product createTestProduct(String name, String description, BigDecimal price, 
                                    Integer stock, Double averageRating, Integer totalReviews) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setAverageRating(averageRating);
        product.setTotalReviews(totalReviews);
        return productRepository.save(product);
    }

    /**
     * Verifies that stock levels have been updated correctly for multiple products.
     * Reduces repetitive assertion code in test methods.
     */
    private void verifyStockUpdates(List<Product> products, List<Integer> expectedStocks) {
        for (int i = 0; i < products.size(); i++) {
            Product updatedProduct = productRepository.findById(products.get(i).getId()).orElseThrow();
            assertThat(updatedProduct.getStock()).isEqualTo(expectedStocks.get(i));
        }
    }

    /**
     * Data holder class for test data.
     * Improves readability by grouping related test entities.
     */
    private static class TestData {
        final User user;
        final List<Product> products;

        TestData(User user, List<Product> products) {
            this.user = user;
            this.products = products;
        }
    }
}
