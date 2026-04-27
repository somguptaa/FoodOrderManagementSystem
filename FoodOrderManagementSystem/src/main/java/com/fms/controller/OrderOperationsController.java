package com.fms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fms.dto.OrderResponseDTO;
import com.fms.dto.OrderStatusUpdateRequest;
import com.fms.entity.Order;
import com.fms.enums.OrderStatus;
import com.fms.service.IOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Manage food orders")
public class OrderOperationsController {

    private final IOrderService service;

    public OrderOperationsController(IOrderService service) {
        this.service = service;
    }

    @Operation(summary = "Create order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody Order order) {
        return new ResponseEntity<>(service.createOrder(order), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @Operation(summary = "Get order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrderById(id));
    }

    @Operation(summary = "Update order")
    @ApiResponse(responseCode = "200", description = "Order updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody Order order) {
        return ResponseEntity.ok(service.updateOrder(id, order));
    }

    @Operation(summary = "Update order status")
    @ApiResponse(responseCode = "200", description = "Order status updated")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "409", description = "Invalid status transition")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(service.updateOrderStatus(id, request.getStatus()));
    }

    @Operation(summary = "Get orders by status")
    @ApiResponse(responseCode = "200", description = "Filtered orders retrieved")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status) {
        return ResponseEntity.ok(service.getOrdersByStatus(status));
    }

    @Operation(summary = "Delete order")
    @ApiResponse(responseCode = "200", description = "Order deleted successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
