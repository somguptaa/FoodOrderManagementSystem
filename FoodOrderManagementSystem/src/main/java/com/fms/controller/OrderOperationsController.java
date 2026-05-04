package com.fms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fms.dto.OrderResponseDTO;
import com.fms.dto.OrderStatusUpdateRequest;
import com.fms.entity.Order;
import com.fms.enums.OrderStatus;
import com.fms.service.IOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "https://incredible-melba-7d58be.netlify.app/")
@Tag(name = "Order Operations", description = "Create, retrieve, update and delete food orders")
public class OrderOperationsController {

    @Autowired
    private IOrderService service;

    @Operation(
        summary = "Create a new order",
        description = "Places a new food order with PENDING status. All items must have a name, quantity >= 1, and price > 0.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Sample order",
                    value = """
                        {
                          "customerName": "Ravi Kumar",
                          "items": [
                            { "productName": "Chicken Biryani", "quantity": 2, "price": 180.00 },
                            { "productName": "Lassi",           "quantity": 1, "price": 60.00  }
                          ]
                        }
                        """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed — check fieldErrors in response",
                     content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody Order order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(order));
    }

    @Operation(
        summary = "Get all orders",
        description = "Returns every order in the system with calculated totals."
    )
    @ApiResponse(responseCode = "200", description = "List of orders returned")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @Operation(
        summary = "Get order by ID",
        description = "Fetches a single order by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "ID of the order to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getOrderById(id));
    }

    @Operation(
        summary = "Update an order",
        description = "Replaces the customerName and items of an existing order. Does not change status — use PATCH /orders/{id}/status for that.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Update order",
                    value = """
                        {
                          "customerName": "Priya Sharma",
                          "items": [
                            { "productName": "Paneer Butter Masala", "quantity": 1, "price": 220.00 },
                            { "productName": "Naan",                  "quantity": 2, "price": 40.00  }
                          ]
                        }
                        """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @Parameter(description = "ID of the order to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Order order) {
        return ResponseEntity.ok(service.updateOrder(id, order));
    }

    @Operation(
    	    summary = "Update order status",
    	    description = """
    	        Transitions the order to a new status. Only valid transitions are allowed:
    	        - PENDING    → CONFIRMED or CANCELLED
    	        - CONFIRMED  → PREPARING or CANCELLED
    	        - PREPARING  → DELIVERED or CANCELLED
    	        - DELIVERED  → terminal, no further transitions
    	        - CANCELLED  → terminal, no further transitions
    	        """,
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        content = @Content(
    	            mediaType = "application/json",
    	            examples = {
    	                @ExampleObject(
    	                    name = "Confirm order",
    	                    value = """
    	                        { "status": "CONFIRMED" }
    	                        """
    	                ),
    	                @ExampleObject(
    	                    name = "Start preparing",
    	                    value = """
    	                        { "status": "PREPARING" }
    	                        """
    	                ),
    	                @ExampleObject(
    	                    name = "Mark delivered",
    	                    value = """
    	                        { "status": "DELIVERED" }
    	                        """
    	                ),
    	                @ExampleObject(
    	                    name = "Cancel order",
    	                    value = """
    	                        { "status": "CANCELLED" }
    	                        """
    	                )
    	            }
    	        )
    	    )
    	)
    	@ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Status updated successfully"),
    	    @ApiResponse(responseCode = "404", description = "Order not found"),
    	    @ApiResponse(responseCode = "409", description = "Invalid status transition — e.g. DELIVERED to PENDING")
    	})
    	@PatchMapping("/{id}/status")
    	public ResponseEntity<OrderResponseDTO> updateOrderStatus(
    	        @Parameter(description = "ID of the order", example = "1")
    	        @PathVariable Long id,
    	        @Valid @RequestBody OrderStatusUpdateRequest request) {
    	    return ResponseEntity.ok(service.updateOrderStatus(id, request.getStatus()));
    	}
    

    @Operation(
        summary = "Get orders by status",
        description = "Filters and returns all orders matching the given status."
    )
    @ApiResponse(responseCode = "200", description = "Filtered list returned")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @Parameter(
                description = "Status to filter by",
                example = "PENDING",
                schema = @Schema(implementation = OrderStatus.class)
            )
            @PathVariable OrderStatus status) {
        return ResponseEntity.ok(service.getOrdersByStatus(status));
    }

    @Operation(
        summary = "Delete an order",
        description = "Permanently removes an order from the system."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @Parameter(description = "ID of the order to delete", example = "1")
            @PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
