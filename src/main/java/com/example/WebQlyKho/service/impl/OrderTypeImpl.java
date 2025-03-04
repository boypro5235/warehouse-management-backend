package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.OrderTypeRequest;
import com.example.WebQlyKho.entity.OrderType;
import com.example.WebQlyKho.repository.OrderTypeRepository;
import com.example.WebQlyKho.service.OrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderTypeImpl implements OrderTypeService {
    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Override
    public List<OrderType> getAllOrderType() {
        return orderTypeRepository.findAll();
    }

    @Override
    public OrderType getOrderTypeById(int id) {
        return orderTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("OrderType not found"));
    }

    @Override
    public OrderType createOrderType(OrderTypeRequest request) {
        OrderType ordertype = OrderType.builder()
                .orderTypeName(request.orderTypeName)
                .createdAt(LocalDateTime.now())
                .build();
        return orderTypeRepository.save(ordertype);
    }

    @Override
    public OrderType updateOrderType(int id, OrderTypeRequest request) {
        OrderType orderType = orderTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("OrderType not found"));
        orderType.setOrderTypeName(request.orderTypeName);
        orderType.setUpdatedAt(LocalDateTime.now());
        return orderTypeRepository.save(orderType);
    }

    @Override
    public void deleteOrderType(int id) {
        OrderType orderType = orderTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("OrderType not found"));
        orderType.setStatus(false);
        orderType.setUpdatedAt(LocalDateTime.now());
        orderTypeRepository.save(orderType);
    }
}
