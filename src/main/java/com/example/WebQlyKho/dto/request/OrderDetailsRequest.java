package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailsRequest {
    @NotNull
    private Integer productId;
    @NotNull
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    @NotNull
    @Positive(message = "Subtotal must be positive")
    private Double subtotal;
    @NotNull
    @Positive(message = "VAT must be positive")
    private float vat;
    @NotNull
    @Positive(message = "Discount must be positive")
    private float discount;
    @NotNull
    @Positive(message = "Total amount must be positive")
    private float totalAmount;
}
