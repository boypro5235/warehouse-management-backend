package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.FinancialReportDTO;
import com.example.WebQlyKho.dto.request.CreateOrderDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.entity.Order;
import com.example.WebQlyKho.entity.OrderDetails;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    Order createOrder(CreateOrderDto createOrderDto, HttpServletRequest request);
    List<OrderDetails> getOrderById(Integer orderId);
    List<Order> getAllOrder();
    List<Order> searchOrders(String orderStatus, Integer orderType, String customer, String fromDate, String toDate);
    Order updateOrder(Integer orderId, CreateOrderDto request, HttpServletRequest httpServletRequest);
    void deleteOrder(DeleteRequest ids);
    List<FinancialReportDTO> FinancialReport(String scope);
 }
