package com.fms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fms.entity.Order;
import com.fms.enums.OrderStatus;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    // Spring Data generates the query automatically from the method name
    List<Order> findByStatus(OrderStatus status);
}