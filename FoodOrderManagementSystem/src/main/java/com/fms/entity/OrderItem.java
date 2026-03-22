package com.fms.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderItem {

    @NotBlank(message = "Product name must not be blank")
    private String productName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    // Convenience method — subtotal for this line item
    public Double getSubtotal() {
        return price * quantity;
    }
}