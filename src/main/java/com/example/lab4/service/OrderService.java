package com.example.lab4.service;

import com.example.lab4.entity.Order;
import com.example.lab4.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;

public interface OrderService {
    Order placeOrder(@Valid OrderServiceImpl.OrderRequest request);
}
