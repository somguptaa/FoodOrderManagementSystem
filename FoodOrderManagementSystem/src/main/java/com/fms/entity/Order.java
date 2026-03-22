package com.fms.entity;

import java.util.ArrayList;
import java.util.List;

import com.fms.enums.OrderStatus;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name must not be blank")
    private String customerName;

    @Enumerated(EnumType.STRING)  // stores "PENDING" in DB, not 0/1/2
    private OrderStatus status = OrderStatus.PENDING;  // default on creation

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    @ElementCollection
    private List<OrderItem> items = new ArrayList<>();

    @Transient
    public Double getTotalAmount() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }
}