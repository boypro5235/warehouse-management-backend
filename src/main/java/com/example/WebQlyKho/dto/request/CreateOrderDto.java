package com.example.WebQlyKho.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOrderDto {
    @NotNull(message = "Order type is required")
    private Integer orderType;
    private String status;
    private String customer;
    private String address;
    @Positive(message = "Kho hang id must be positive")
    @NotNull(message = "Kho hang id is required")
    private Integer khohangId;
    @Positive(message = "Total amount must be positive")
    private float totalAmount;
    @Positive(message = "VAT must be positive")
    private float vat;
    @Positive(message = "Discount must be positive")
    private float discount;
    @Positive(message = "Final amount must be positive")
    private float finalAmount;
    private List<OrderDetailsRequest> orderDetails;
}
