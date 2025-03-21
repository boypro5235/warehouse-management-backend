package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryDto {
    @NotNull(message = "Category name is required")
    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;
}
