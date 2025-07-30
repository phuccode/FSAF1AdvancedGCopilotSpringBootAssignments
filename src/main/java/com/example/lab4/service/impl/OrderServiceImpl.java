package com.example.lab4.service.impl;

import com.example.lab4.entity.Order;
import com.example.lab4.entity.OrderItem;
import com.example.lab4.entity.Product;
import com.example.lab4.entity.User;
import com.example.lab4.exception.InsufficientStockException;
import com.example.lab4.exception.OrderValidationException;
import com.example.lab4.exception.ProductNotFoundException;
import com.example.lab4.exception.UserNotFoundException;
import com.example.lab4.repository.OrderRepository;
import com.example.lab4.repository.ProductRepository;
import com.example.lab4.repository.UserRepository;
import com.example.lab4.service.AuditService;
import com.example.lab4.service.OrderService;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public record OrderRequest(
        @NotNull Long userId,
        @NotEmpty Map<Long, Integer> productQuantities  // Map of productId to quantity
    ) {}

    private Order buildOrder(OrderRequest request) {
        validateOrderRequest(request);
        
        User user = verifyUserExists(request.userId());
        Order order = createEmptyOrder(user);
        
        Map<Long, Product> products = fetchAndValidateProducts(request.productQuantities().keySet());
        
        processOrderItems(request, order, products);
        
        validateFinalOrder(order);
        
        return order;
    }
    
    private void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            throw new OrderValidationException("Order request cannot be null");
        }
        if (request.productQuantities() == null || request.productQuantities().isEmpty()) {
            throw new OrderValidationException("Order must contain at least one product");
        }
    }
    
    private Order createEmptyOrder(User user) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    
    private Map<Long, Product> fetchAndValidateProducts(Set<Long> productIds) {
        var foundProducts = productRepository.findAllById(productIds);
        
        // Verify all products were found
        if (foundProducts.size() != productIds.size()) {
            var missingIds = productIds.stream()
                .filter(id -> !foundProducts.stream().anyMatch(p -> p.getId().equals(id)))
                .collect(Collectors.toList());
            throw new ProductNotFoundException("Some products were not found: " + missingIds);
        }
        
        // Create map for efficient lookup
        return foundProducts.stream()
            .collect(Collectors.toMap(
                Product::getId,
                product -> product,
                (existing, replacement) -> existing // Handle unlikely duplicates
            ));
    }
    
    private void processOrderItems(OrderRequest request, Order order, Map<Long, Product> products) {
        request.productQuantities().forEach((productId, quantity) -> {
            validateQuantity(productId, quantity);
            
            Product product = products.get(productId);
            validateProduct(product, productId);
            validateProductInventory(product, quantity);
            
            OrderItem orderItem = createOrderItem(product, quantity);
            order.addOrderItem(orderItem);
            
            // Update product stock (will be persisted later)
            product.setStock(product.getStock() - quantity);
        });
    }
    
    private void validateQuantity(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new OrderValidationException("Invalid quantity for product ID: " + productId);
        }
    }
    
    private void validateProduct(Product product, Long productId) {
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }
        if (product.getStock() == null) {
            throw new OrderValidationException("Product stock is not set for: " + product.getName());
        }
        if (product.getPrice() == null) {
            throw new OrderValidationException("Product price is not set for: " + product.getName());
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderValidationException("Invalid price for product: " + product.getName());
        }
    }
    
    private void validateProductInventory(Product product, Integer quantity) {
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(String.format(
                "Insufficient stock for product: %s. Available: %d, Requested: %d",
                product.getName(), product.getStock(), quantity
            ));
        }
    }
    
    private OrderItem createOrderItem(Product product, Integer quantity) {
        OrderItem orderItem = new OrderItem(product, quantity);
        if (orderItem.getSubTotal() == null || orderItem.getSubTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderValidationException("Invalid subtotal for product: " + product.getName());
        }
        return orderItem;
    }
    
    private void validateFinalOrder(Order order) {
        if (order.getOrderItems().isEmpty()) {
            throw new OrderValidationException("Order must contain at least one item");
        }
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderValidationException("Order total amount must be greater than zero");
        }
    }
    
    @Override
    @Transactional(
        isolation = Isolation.REPEATABLE_READ,
        rollbackFor = {Exception.class},
        timeout = 30
    )
    public Order placeOrder(@Valid OrderRequest request) {
        Order order = null;
        try {
            // Step 1: Build and validate the order (no DB changes yet)
            order = buildOrder(request);
            
            // Step 2: Perform stock validation in a pessimistic lock to prevent overselling
            validateAndLockStock(order);
            
            // Step 3: Save the order and update stock atomically
            Order savedOrder = saveOrderAndUpdateStock(order);
            
            // Step 4: Audit logging (outside transaction)
            auditOrderSuccess(savedOrder);
            
            return savedOrder;
            
        } catch (Exception e) {
            // Audit the failure (outside transaction)
            auditOrderFailure(order, e);
            
            // Rethrow appropriate exception to trigger rollback
            if (e instanceof OrderValidationException 
                || e instanceof ProductNotFoundException 
                || e instanceof InsufficientStockException) {
                throw e;
            }
            throw new OrderValidationException("Order placement failed: " + e.getMessage(), e);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void auditOrderSuccess(Order savedOrder) {
        try {
            auditService.logSecurityEvent(
                "ORDER_PLACED",
                String.format("Order %d placed by user %d", 
                    savedOrder.getId(), 
                    savedOrder.getUser().getId())
            );
        } catch (Exception e) {
            // Log audit failure but don't affect the order transaction
            auditService.logSecurityEvent(
                "AUDIT_FAILURE",
                "Failed to audit order placement: " + e.getMessage()
            );
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void auditOrderFailure(Order order, Exception e) {
        try {
            String orderInfo = order != null ? 
                String.format(" for user %d", order.getUser().getId()) : "";
            
            auditService.logSecurityEvent(
                "ORDER_FAILED",
                "Order placement failed" + orderInfo + ": " + e.getMessage()
            );
        } catch (Exception auditError) {
            auditService.logSecurityEvent(
                "AUDIT_FAILURE",
                "Failed to audit order failure: " + auditError.getMessage()
            );
        }
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    protected void validateAndLockStock(Order order) {
        // Re-validate stock with pessimistic locking to prevent race conditions
        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findByIdWithPessimisticLock(item.getProduct().getId())
                .orElseThrow(() -> new ProductNotFoundException(item.getProduct().getId()));
                
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(String.format(
                    "Stock changed! Product: %s. Available: %d, Requested: %d",
                    product.getName(), product.getStock(), item.getQuantity()
                ));
            }
            
            // Update the product reference in the order item to the locked version
            item.setProduct(product);
        }
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    protected Order saveOrderAndUpdateStock(Order order) {
        // Stock levels are already updated in processProductQuantities()
        // Just save the order and persist the product changes
        order.getOrderItems().forEach(item -> {
            productRepository.save(item.getProduct());
        });
        
        // Save the order
        return orderRepository.save(order);
        }

    private User verifyUserExists(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
