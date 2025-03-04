package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;

public class OrderTypeRequest {
    @NotBlank(message = "Tên loại đơn hàng không được để trống")
    public String orderTypeName;
}
