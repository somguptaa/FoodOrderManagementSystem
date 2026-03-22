package com.fms.service;

import java.util.List;

import com.fms.dto.OrderResponseDTO;
import com.fms.entity.Order;
import com.fms.enums.OrderStatus;

public interface IOrderService {

    OrderResponseDTO createOrder(Order order);
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO getOrderById(Long id);
    OrderResponseDTO updateOrder(Long id, Order order);
    void deleteOrder(Long id);

    // New — dedicated status transition method
    OrderResponseDTO updateOrderStatus(Long id, OrderStatus newStatus);

    // New — filter orders by status
    List<OrderResponseDTO> getOrdersByStatus(OrderStatus status);
}