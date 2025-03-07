package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSupplierDto {
    @NotBlank(message = "Supplier name is required")
    @NotNull(message = "Supplier name is required")
    private String supplierName;
    @NotNull(message = "Contact info is required")
    private String contactInfo;
}
