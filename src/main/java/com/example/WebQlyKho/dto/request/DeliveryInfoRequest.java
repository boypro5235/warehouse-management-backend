package com.example.WebQlyKho.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryInfoRequest {
    private Integer orderId;
    private String deliveryStatus;
    private boolean status;
}
