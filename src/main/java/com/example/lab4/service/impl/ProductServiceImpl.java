package com.example.lab4.service.impl;

import com.example.lab4.entity.Product;
import com.example.lab4.exception.ProductNotFoundException;
import com.example.lab4.repository.ProductRepository;
import com.example.lab4.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Cacheable(
        cacheNames = "product-details", 
        key = "#id",
        unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @CacheEvict(
        cacheNames = "product-details", 
        key = "#product.id"
    )
    @Transactional
    public Product updateProduct(Product product) {
        // Ensure the product exists before updating
        if (!productRepository.existsById(product.getId())) {
            throw new ProductNotFoundException(product.getId());
        }
        return productRepository.save(product);
    }
}
