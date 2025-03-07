package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDskhohangDto {
    @NotBlank(message = "Kho hang name is required")
    @NotNull(message = "Kho hang name is required")
    private String name;

    @NotNull(message = "Kho hang address is required")
    private String address;

    @NotNull(message = "Kho hang contact is required")
    private String contact;
}
