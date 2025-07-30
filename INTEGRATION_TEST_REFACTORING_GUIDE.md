# Integration Test Refactoring: Extracting Long Arrange Blocks

This document demonstrates how to refactor integration test methods by extracting long "Arrange" blocks into helper methods, improving code readability and maintainability.

## Problem: Long Arrange Blocks

Integration tests often require extensive setup of test data, leading to long and repetitive "Arrange" blocks that make tests difficult to read and maintain.

## Example: Before Refactoring

Here's an example of an integration test with a **long Arrange block**:

```java
@Test
@DisplayName("Should create order with multiple products successfully")
void shouldCreateOrderWithMultipleProducts() {
    // Arrange - This is the long setup block that needs refactoring
    // Create test user
    User testUser = new User();
    testUser.setUsername("testuser");
    testUser.setEmail("testuser@example.com");
    testUser.setPassword("hashedpassword123");
    testUser.setCreatedDate(LocalDateTime.now());
    testUser = userRepository.save(testUser);

    // Create first test product
    Product product1 = new Product();
    product1.setName("Laptop");
    product1.setDescription("High-performance laptop");
    product1.setPrice(new BigDecimal("999.99"));
    product1.setStock(10);
    product1.setAverageRating(4.5);
    product1.setTotalReviews(100);
    product1 = productRepository.save(product1);

    // Create second test product
    Product product2 = new Product();
    product2.setName("Mouse");
    product2.setDescription("Wireless optical mouse");
    product2.setPrice(new BigDecimal("29.99"));
    product2.setStock(50);
    product2.setAverageRating(4.2);
    product2.setTotalReviews(75);
    product2 = productRepository.save(product2);

    // Create third test product
    Product product3 = new Product();
    product3.setName("Keyboard");
    product3.setDescription("Mechanical gaming keyboard");
    product3.setPrice(new BigDecimal("149.99"));
    product3.setStock(25);
    product3.setAverageRating(4.8);
    product3.setTotalReviews(200);
    product3 = productRepository.save(product3);

    // Prepare order request with multiple products
    Map<Long, Integer> productQuantities = Map.of(
        product1.getId(), 1,
        product2.getId(), 2,
        product3.getId(), 1
    );

    OrderServiceImpl.OrderRequest orderRequest = new OrderServiceImpl.OrderRequest(
        testUser.getId(),
        productQuantities
    );

    // Act
    Order createdOrder = orderService.placeOrder(orderRequest);

    // Assert
    assertThat(createdOrder).isNotNull();
    assertThat(createdOrder.getId()).isNotNull();
    // ... more assertions
}
```

**Problems with this approach:**
- **Long and repetitive**: 40+ lines of setup code
- **Hard to read**: The actual test logic is buried in setup code
- **Duplication**: Similar setup code repeated across multiple tests
- **Maintenance burden**: Changes to entity structure require updates in multiple places

## Solution: After Refactoring

Here's the same test **after refactoring** with helper methods:

```java
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

// Helper Methods - Extracted from long Arrange blocks

/**
 * Creates initial test data with a user and multiple products.
 * This helper method extracts the repetitive setup code from test methods.
 */
private TestData setupInitialData() {
    User user = createTestUser("testuser", "testuser@example.com");
    
    List<Product> products = List.of(
        createTestProduct("Laptop", "High-performance laptop", 
            new BigDecimal("999.99"), 10, 4.5, 100),
        createTestProduct("Mouse", "Wireless optical mouse", 
            new BigDecimal("29.99"), 50, 4.2, 75),
        createTestProduct("Keyboard", "Mechanical gaming keyboard", 
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
```

## Benefits of Refactoring

### 1. **Improved Readability**
- The test method is now **10 lines** instead of 40+
- The test intent is immediately clear
- Arrange, Act, Assert sections are distinct

### 2. **Reduced Duplication**
- Common setup logic is centralized in helper methods
- Multiple tests can reuse the same helper methods
- Changes to entity structure only need to be made in one place

### 3. **Better Maintainability**
- Helper methods have clear, descriptive names and documentation
- Each helper method has a single responsibility
- Test data creation is parameterized and flexible

### 4. **Enhanced Reusability**
- `setupInitialData()` can be used by multiple test methods
- `createTestUser()` and `createTestProduct()` are highly reusable
- Verification methods like `verifyStockUpdates()` eliminate repetitive assertions

## Refactoring Guidelines

### 1. **Extract Common Setup Logic**
- Look for repetitive entity creation code
- Create parameterized helper methods for flexibility
- Use descriptive method names that explain what they do

### 2. **Create Data Holder Classes**
- Use simple classes to group related test data
- Makes test data passing between methods cleaner
- Improves type safety compared to returning arrays or maps

### 3. **Extract Verification Logic**
- Create helper methods for complex assertions
- Parameterize verification methods for reusability
- Keep assertion logic DRY (Don't Repeat Yourself)

### 4. **Use Meaningful Names**
- Method names should explain their purpose clearly
- Include JavaDoc comments for complex helper methods
- Use intention-revealing variable names

## When to Apply This Pattern

This refactoring pattern is most beneficial when you have:

- **Long setup blocks** (>15-20 lines)
- **Repetitive setup code** across multiple tests
- **Complex entity creation** with many properties
- **Multiple test scenarios** requiring similar data
- **Difficult-to-read tests** due to setup complexity

## Key Takeaways

1. **Long Arrange blocks are a code smell** in integration tests
2. **Helper methods improve test readability** and maintainability  
3. **Centralized setup logic** reduces duplication and eases maintenance
4. **Data holder classes** make test data management cleaner
5. **Parameterized helpers** provide flexibility for different test scenarios

This refactoring approach transforms hard-to-read integration tests into clean, maintainable, and reusable test code.
