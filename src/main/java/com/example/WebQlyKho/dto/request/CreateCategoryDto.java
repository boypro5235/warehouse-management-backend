package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryDto {
    @NotNull(message = "Category name is required")
    @NotBlank(message = "Category name is required")
    private String categoryName;
}
