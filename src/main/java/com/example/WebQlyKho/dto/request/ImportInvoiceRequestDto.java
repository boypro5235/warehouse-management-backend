package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class ImportInvoiceRequestDto {
    @NotNull(message = "Supplier Id is required")
    @Positive(message = "Supplier Id must be positive")
    private int supplierId;

    @NotNull(message = "khohangId is required")
    @Positive(message = "khohangId must be positive")
    private Integer khohangId;

    private String importDate;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private double totalAmount;

    private float vat;

    private float discount;

    private double finalAmount;

    private List<ImportDetailRequestDto> importDetails;

}
