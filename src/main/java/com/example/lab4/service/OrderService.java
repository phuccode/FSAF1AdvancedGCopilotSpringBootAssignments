package com.example.lab4.service;

import com.example.lab4.entity.Order;
import com.example.lab4.service.impl.OrderServiceImpl;

public interface OrderService {
    Order placeOrder(OrderServiceImpl.OrderRequest request);
}
