package com.fms.dto;

import java.util.List;

import com.fms.entity.Order;
import com.fms.entity.OrderItem;
import com.fms.enums.OrderStatus;

public class OrderResponseDTO {

    private Long id;
    private String customerName;
    private OrderStatus status;
    private List<OrderItem> items;
    private Double totalAmount;

    public static OrderResponseDTO from(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.id           = order.getId();
        dto.customerName = order.getCustomerName();
        dto.status       = order.getStatus();
        dto.items        = order.getItems();
        dto.totalAmount  = order.getTotalAmount();
        return dto;
    }

    public Long getId()                { return id; }
    public String getCustomerName()    { return customerName; }
    public OrderStatus getStatus()     { return status; }
    public List<OrderItem> getItems()  { return items; }
    public Double getTotalAmount()     { return totalAmount; }
}