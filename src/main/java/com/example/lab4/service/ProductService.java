package com.example.lab4.service;

import com.example.lab4.entity.Product;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findProductById(Long id);
    Product updateProduct(Product product);
}
