package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.FinancialReportDTO;
import com.example.WebQlyKho.dto.request.CreateOrderDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.Order;
import com.example.WebQlyKho.service.OrderService;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // Tạo đơn hàng
    @PostMapping("/create")
    public ResponseEntity<Object> createOrder(@RequestBody CreateOrderDto request, HttpServletRequest httpRequest) {
        Order order = orderService.createOrder(request, httpRequest);
        return APIResponse.responseBuilder(order, "Order created successfully", HttpStatus.CREATED);
    }

    // Cập nhật đơn hàng
    @PutMapping("/update/{orderId}")
    public ResponseEntity<Object> updateOrder(@PathVariable Integer orderId, @RequestBody CreateOrderDto request, HttpServletRequest httpRequest) {
        Order order = orderService.updateOrder(orderId, request, httpRequest);
        return APIResponse.responseBuilder(order, "Order updated successfully", HttpStatus.OK);
    }

    // Xóa đơn hàng
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteOrders(@RequestBody DeleteRequest orderIds) {
        orderService.deleteOrder(orderIds);
        return APIResponse.responseBuilder(null, "Orders deleted successfully", HttpStatus.OK);
    }

    // Lấy tất cả đơn hàng
    @GetMapping("/getAllOrders")
    public ResponseEntity<Object> getAllOrders() {
        List<Order> orders = orderService.getAllOrder();
        return APIResponse.responseBuilder(orders, "Orders fetched successfully", HttpStatus.OK);
    }

    // Lấy đơn hàng theo ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        return APIResponse.responseBuilder(order, "Order fetched successfully", HttpStatus.OK);
    }

    // Tìm kiếm đơn hàng với các điều kiện
    @GetMapping("/search")
    public ResponseEntity<Object> searchOrders(
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) String customer,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        List<Order> orders = orderService.searchOrders(orderStatus, orderType, customer, fromDate, toDate);
        return APIResponse.responseBuilder(orders, "Orders found successfully", HttpStatus.OK);
    }

    @GetMapping("/getFinacialReport/{scope}")
    public ResponseEntity<Object> getFinancialReport(@PathVariable String scope) {
        List<FinancialReportDTO> report = orderService.FinancialReport(scope);
        return APIResponse.responseBuilder(report, "Order fetched successfully", HttpStatus.OK);
    }

}
