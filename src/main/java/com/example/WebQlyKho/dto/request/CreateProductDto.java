package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductDto {
    @Positive(message = "Category Id must be positive")
    @NotNull(message = "Category Id is required")
    private String categoryId;

    @NotNull(message = "Product name is required")
    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private double price;
}
