package com.example.eureka_order.service;

import com.example.eureka_order.dto.OrderDto;
import com.example.eureka_order.jpa.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);


}
