package com.fms.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.dto.OrderResponseDTO;
import com.fms.entity.Order;
import com.fms.enums.OrderStatus;
import com.fms.exception.InvalidStatusTransitionException;
import com.fms.exception.OrderNotFoundException;
import com.fms.repository.IOrderRepository;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private IOrderRepository repository;

    @Override
    public OrderResponseDTO createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING); // always start as PENDING
        return OrderResponseDTO.from(repository.save(order));
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return repository.findAll().stream()
                .map(OrderResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        return OrderResponseDTO.from(findOrThrow(id));
    }

    @Override
    public OrderResponseDTO updateOrder(Long id, Order updatedOrder) {
        Order existing = findOrThrow(id);
        existing.setCustomerName(updatedOrder.getCustomerName());
        existing.setItems(updatedOrder.getItems());
        // status is NOT updated here — use updateOrderStatus() for that
        return OrderResponseDTO.from(repository.save(existing));
    }

    @Override
    public void deleteOrder(Long id) {
        repository.delete(findOrThrow(id));
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = findOrThrow(id);
        OrderStatus current = order.getStatus();

        // Guard: validate the transition is allowed
        if (!current.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(current, newStatus);
        }

        order.setStatus(newStatus);
        return OrderResponseDTO.from(repository.save(order));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        return repository.findByStatus(status).stream()
                .map(OrderResponseDTO::from)
                .collect(Collectors.toList());
    }

    // Private helper — avoids repeating findById + orElseThrow everywhere
    private Order findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}