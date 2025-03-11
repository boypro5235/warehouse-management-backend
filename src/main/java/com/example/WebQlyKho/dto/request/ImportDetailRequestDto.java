package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ImportDetailRequestDto {
    @NotNull(message = "invoicesId is required")
    @Positive(message = "invoicesId must be positive")
    private Integer invoicesId;
    @NotNull(message = "productId is required")
    @Positive(message = "productId must be positive")
    private Integer productId;
    @NotNull(message = "quantity is required")
    private Integer quantity;
    @NotNull(message = "price is required")
    private Double price;
    @NotNull(message = "khohangId is required")
    @Positive(message = "khohangId must be positive")
    private Integer khohangId;
    private Double subtotal;
    private float vat;
    private float discount;
    private double totalAmount;
}
