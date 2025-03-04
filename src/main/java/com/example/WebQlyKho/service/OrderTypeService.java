package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.OrderTypeRequest;
import com.example.WebQlyKho.entity.OrderType;

import java.util.List;

public interface OrderTypeService {
    List<OrderType> getAllOrderType();
    OrderType getOrderTypeById(int id);
    OrderType createOrderType(OrderTypeRequest request);
    OrderType updateOrderType(int id, OrderTypeRequest request);
    void deleteOrderType(int id);
}
