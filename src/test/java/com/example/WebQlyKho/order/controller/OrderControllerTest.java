package com.example.WebQlyKho.order.controller;

import com.example.WebQlyKho.controller.OrderController;
import com.example.WebQlyKho.dto.request.CreateOrderDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.entity.Order;
import com.example.WebQlyKho.service.OrderService;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateOrder() throws Exception {
        // Mock dữ liệu
        CreateOrderDto request = new CreateOrderDto();
        Order order = new Order();
        order.setOrderId(1);
        order.setOrderStatus("Created");

        when(orderService.createOrder(any(CreateOrderDto.class), any())).thenReturn(order);

        // Thực hiện yêu cầu POST và kiểm tra phản hồi
        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.orderId").value(1));
    }

    @Test
    void testUpdateOrder() throws Exception {
        // Mock dữ liệu
        CreateOrderDto request = new CreateOrderDto();
        Order order = new Order();
        order.setOrderId(1);
        order.setOrderStatus("Updated");

        when(orderService.updateOrder(eq(1), any(CreateOrderDto.class), any())).thenReturn(order);

        // Thực hiện yêu cầu PUT và kiểm tra phản hồi
        mockMvc.perform(put("/api/orders/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order updated successfully"))
                .andExpect(jsonPath("$.data.orderId").value(1));
    }

    @Test
    void testDeleteOrders() throws Exception {
        // Mock dữ liệu
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIds(Arrays.asList(1, 2, 3));

        doNothing().when(orderService).deleteOrder(any(DeleteRequest.class));

        // Thực hiện yêu cầu DELETE và kiểm tra phản hồi
        mockMvc.perform(delete("/api/orders/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Orders deleted successfully"));
    }

    @Test
    void testGetAllOrders() throws Exception {
        // Mock dữ liệu
        Order order1 = new Order();
        order1.setOrderId(1);
        Order order2 = new Order();
        order2.setOrderId(2);
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderService.getAllOrder()).thenReturn(orders);

        // Thực hiện yêu cầu GET và kiểm tra phản hồi
        mockMvc.perform(get("/api/orders/getAllOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Orders fetched successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetOrderById() throws Exception {
        // Mock dữ liệu
        Order order = new Order();
        order.setOrderId(1);

        when(orderService.getOrderById(1)).thenReturn(order);

        // Thực hiện yêu cầu GET và kiểm tra phản hồi
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order fetched successfully"))
                .andExpect(jsonPath("$.data.orderId").value(1));
    }
}
