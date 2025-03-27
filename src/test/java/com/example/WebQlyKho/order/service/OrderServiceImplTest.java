package com.example.WebQlyKho.order.service;

import com.example.WebQlyKho.dto.request.CreateOrderDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.request.OrderDetailsRequest;
import com.example.WebQlyKho.entity.Order;
import com.example.WebQlyKho.entity.OrderDetails;
import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.repository.*;
import com.example.WebQlyKho.service.impl.OrderServiceImpl;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailsRepository orderDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private DskhohangRepository dskhohangRepository;

    @Mock
    private OrderTypeRepository orderTypeRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private HttpServletRequest request;

    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order();
        order.setOrderId(1);
        order.setOrderStatus("Pending");
        order.setOrderType(1);
        order.setTotalAmount(1000.0);
        order.setCreatedAt(LocalDateTime.now());

        user = new User();
        user.setUsername("testUser");
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        Order result = orderService.getOrderById(1);
        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(1));
    }

    @Test
    void testDeleteImportInvoice_Success() {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIds(Arrays.asList(1, 2));

        Order order2 = new Order();
        order2.setOrderId(2);

        when(orderRepository.findAllById(Arrays.asList(1, 2))).thenReturn(Arrays.asList(order, order2));
        doNothing().when(orderRepository).deleteAll(anyList());

        assertDoesNotThrow(() -> orderService.deleteImportInvoice(deleteRequest));
    }

    @Test
    void testDeleteImportInvoice_NotFound() {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIds(Arrays.asList(1, 2));
        when(orderRepository.findAllById(Arrays.asList(1, 2))).thenReturn(Collections.singletonList(order));
        assertThrows(EntityNotFoundException.class, () -> orderService.deleteImportInvoice(deleteRequest));
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        List<Order> orders = orderService.getAllOrder();
        assertEquals(1, orders.size());
    }

    @Test
    void testSearchOrders() {
        when(orderRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(order));
        List<Order> result = orderService.searchOrders("Pending", 1, "John", "2024-01-01", "2024-12-31");
        assertEquals(1, result.size());
    }

    @Test
    void testSearchOrders_InvalidDate() {
        assertThrows(IllegalArgumentException.class, () -> orderService.searchOrders("Pending", 1, "John", "2024-13-01", "2024-12-31"));
    }
} 