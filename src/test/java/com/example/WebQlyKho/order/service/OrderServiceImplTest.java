package com.example.WebQlyKho.order.service;

import com.example.WebQlyKho.dto.request.CreateOrderDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.request.OrderDetailsRequest;
import com.example.WebQlyKho.entity.*;
import com.example.WebQlyKho.repository.*;
import com.example.WebQlyKho.service.impl.OrderServiceImpl;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

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
    void setup() {
        Mockito.reset(orderDetailRepository);
    }

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


    //getAllOrders()
    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        List<Order> orders = orderService.getAllOrder();
        assertEquals(1, orders.size());
    }

    //getOrderById()
    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        List<OrderDetails> result = orderService.getOrderById(1);
        assertNotNull(result);
        assertEquals(order.getOrderId(), result.get(1).getOrder().getOrderId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(1));
    }

    //DeleteOrder()
    @Test
    void testDeleteOrder_Success() {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIds(Arrays.asList(1, 2));

        Order order2 = new Order();
        order2.setOrderId(2);

        when(orderRepository.findAllById(Arrays.asList(1, 2))).thenReturn(Arrays.asList(order, order2));
        doNothing().when(orderRepository).deleteAll(anyList());

        assertDoesNotThrow(() -> orderService.deleteOrder(deleteRequest));
    }

    @Test
    void testDeleteOrder_NotFound() {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIds(Arrays.asList(1, 2));
        when(orderRepository.findAllById(Arrays.asList(1, 2))).thenReturn(Collections.singletonList(order));
        assertThrows(EntityNotFoundException.class, () -> orderService.deleteOrder(deleteRequest));
    }

    //createOrder()
    @Test
    void testCreateOrder_InvalidOrderType() {
        // Given: OrderType không hợp lệ (ví dụ: 3)
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setOrderType(3);

        when(request.getUserPrincipal()).thenReturn(() -> "testUser");

        // When & Then: Phương thức phải ném IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(createOrderDto, request));

        assertEquals("OrderType không hợp lệ! Chỉ chấp nhận giá trị 1 hoặc 2.", exception.getMessage());
    }

    @Test
    void testCreateOrder_Success_OrderType1() {
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setOrderType(1);
        createOrderDto.setTotalAmount(500);
        createOrderDto.setKhohangId(1); // Kho hàng ID

        // Mock OrderDetails to avoid NullPointerException
        List<OrderDetailsRequest> orderDetailsList = new ArrayList<>();
        OrderDetailsRequest detailsRequest = new OrderDetailsRequest();
        detailsRequest.setProductId(1);
        detailsRequest.setQuantity(2);
        orderDetailsList.add(detailsRequest);

        createOrderDto.setOrderDetails(orderDetailsList); // Add orderDetails

        // Mock Khohang object
        Dskhohang mockKhohang = new Dskhohang(); // Mocking the Khohang object

        // Mock behavior for repositories
        when(request.getUserPrincipal()).thenReturn(() -> "testUser");
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(mockKhohang));
        when(orderRepository.save(any(Order.class))).thenReturn(order); // Ensure a non-null order is returned

        // Call the service method
        Order result = orderService.createOrder(createOrderDto, request);

        // Assert the result is not null
        assertNotNull(result);

        // Optionally, assert specific properties to ensure correctness
        assertEquals(1, result.getOrderType());
        assertNotNull(result.getCreatedAt());
        assertEquals("Pending", result.getOrderStatus()); // Check default status if applicable
    }

    @Test
    void testCreateOrder_Success_OrderType2() {
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setOrderType(2);
        createOrderDto.setCustomer("Customer A");
        createOrderDto.setAddress("123 Street");
        createOrderDto.setTotalAmount(600);

        // Thêm danh sách orderDetails để tránh lỗi NullPointerException
        List<OrderDetailsRequest> orderDetailsList = new ArrayList<>();
        OrderDetailsRequest detailsRequest = new OrderDetailsRequest();
        detailsRequest.setProductId(2);
        detailsRequest.setQuantity(3);
        orderDetailsList.add(detailsRequest);

        createOrderDto.setOrderDetails(orderDetailsList);

        when(request.getUserPrincipal()).thenReturn(() -> "testUser");
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setOrderId(100); // Giả lập ID khi lưu
            return savedOrder;
        });

        Order result = orderService.createOrder(createOrderDto, request);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.getOrderType());
        assertEquals("Customer A", result.getCustomer());
        assertEquals("123 Street", result.getAddress());
        assertNull(result.getDskhohang(), "Dskhohang should be null when orderType is 2");
    }

    //updateOrder()
    @Test
    void testUpdateOrder_OrderType2_UpdateDetails_Success() {
        int orderId = 1;

        // Mock request
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        // Mock user
        User mockUser = new User();
        when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // Mock CreateOrderDto
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setOrderType(2);
        createOrderDto.setCustomer("Customer A");
        createOrderDto.setAddress("123 Street");
        createOrderDto.setTotalAmount(500);
        createOrderDto.setVat(10);
        createOrderDto.setDiscount(5);
        createOrderDto.setFinalAmount(505);

        // OrderDetails mới gửi lên (có 1 sản phẩm update và 1 sản phẩm mới)
        List<OrderDetailsRequest> orderDetailsRequests = List.of(
                new OrderDetailsRequest(1, 3, 200.0, 10.0f, 5.0f, 205.0f), // Update số lượng
                new OrderDetailsRequest(2, 1, 100.0, 5.0f, 2.0f, 103.0f)  // Thêm mới
        );
        createOrderDto.setOrderDetails(orderDetailsRequests);

        // Mock sản phẩm
        Product product1 = new Product(); product1.setProductId(1);
        Product product2 = new Product(); product2.setProductId(2);
        Mockito.lenient().when(productRepository.getReferenceById(1)).thenReturn(product1);
        Mockito.lenient().when(productRepository.getReferenceById(2)).thenReturn(product2);

        // Mock Order hiện tại trong DB (chứa productId = 1)
        Order existingOrder = new Order();
        existingOrder.setOrderId(orderId);
        existingOrder.setOrderType(2);
        existingOrder.setCustomer("Customer A");
        existingOrder.setAddress("123 Street");
        existingOrder.setTotalAmount(400);
        existingOrder.setOrderStatus("Pending");

        // Mock OrderDetails đã tồn tại
        OrderDetails existedOrderDetail = new OrderDetails();
        existedOrderDetail.setOrder(existingOrder);
        existedOrderDetail.setProduct(product1);
        existedOrderDetail.setQuantity(2);
        existedOrderDetail.setSubtotal(200.0);
        existedOrderDetail.setTotalAmount(200.0);
        existedOrderDetail.setDiscount(5.0f);

        // Mock dữ liệu từ repository
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer testToken");
        when(jwtTokenProvider.getUsernameFromToken("testToken")).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(new User());

        when(orderRepository.findByOrderId(orderId)).thenReturn(existingOrder);
        when(orderDetailRepository.findByOrderId(orderId)).thenReturn(List.of(existedOrderDetail));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        // Gọi phương thức updateOrder()
        Order result = orderService.updateOrder(orderId, createOrderDto, mockRequest);

        // Kiểm tra Order
        assertNotNull(result);
        assertEquals(2, result.getOrderType());
        assertEquals("Customer A", result.getCustomer());
        assertEquals("123 Street", result.getAddress());

        // Kiểm tra OrderDetails đã cập nhật
        verify(orderDetailRepository, times(1)).saveAll(anyList());
    }



    @Test
    void testUpdateOrder_Success() {
        int orderId = 1;

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setOrderType(1);
        createOrderDto.setTotalAmount(500);
        createOrderDto.setKhohangId(1);
        createOrderDto.setStatus("Completed");
        createOrderDto.setOrderDetails(List.of(new OrderDetailsRequest(1, 10, 100.0, 5.0f, 2.0f, 108.0f)));

        Order existingOrder = new Order();
        existingOrder.setOrderId(orderId);
        existingOrder.setOrderStatus("Pending");

        Order updatedOrder = new Order();
        updatedOrder.setOrderId(orderId);
        updatedOrder.setOrderStatus("Completed");

        Dskhohang mockKhohang = new Dskhohang();

        when(orderRepository.findByOrderId(orderId)).thenReturn(existingOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(mockKhohang));
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");
        when(jwtTokenProvider.getUsernameFromToken("testToken")).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(new User());

        Order result = orderService.updateOrder(orderId, createOrderDto, request);

        assertNotNull(result, "Order should not be null");
        assertEquals("Completed", result.getOrderStatus());

        verify(orderRepository).findByOrderId(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testUpdateOrder_NotFound() {
        int orderId = 1;
        Order updatedOrder = new Order();
        updatedOrder.setOrderStatus("Completed");

        when(orderRepository.findByOrderId(orderId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrder(orderId, new CreateOrderDto(), request));
    }

    @Test
    void testUpdateOrder_FilterDeletedOrderDetails() {
        int orderId = 1;

        // Mock request
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        // Mock user
        User mockUser = new User();
        when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // Mock CreateOrderDto
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setOrderType(2);
        createOrderDto.setCustomer("Customer A");
        createOrderDto.setAddress("123 Street");
        createOrderDto.setTotalAmount(500);
        createOrderDto.setVat(10);
        createOrderDto.setDiscount(5);
        createOrderDto.setFinalAmount(505);

        // OrderDetails mới gửi lên (không chứa productId = 1)
        List<OrderDetailsRequest> orderDetailsRequests = List.of(
                new OrderDetailsRequest(2, 1, 100.0, 5.0f, 2.0f, 103.0f)  // Thêm mới
        );
        createOrderDto.setOrderDetails(orderDetailsRequests);

        // Mock sản phẩm
        Product product1 = new Product(); product1.setProductId(1);
        Product product2 = new Product(); product2.setProductId(2);
        Mockito.lenient().when(productRepository.getReferenceById(1)).thenReturn(product1);
        Mockito.lenient().when(productRepository.getReferenceById(2)).thenReturn(product2);

        // Mock Order hiện tại trong DB (chứa productId = 1)
        Order existingOrder = new Order();
        existingOrder.setOrderId(orderId);
        existingOrder.setOrderType(2);
        existingOrder.setCustomer("Customer A");
        existingOrder.setAddress("123 Street");
        existingOrder.setTotalAmount(400);
        existingOrder.setOrderStatus("Pending");

        // Mock OrderDetails đã tồn tại
        OrderDetails existedOrderDetail = new OrderDetails();
        existedOrderDetail.setOrder(existingOrder);
        existedOrderDetail.setProduct(product1);
        existedOrderDetail.setQuantity(2);
        existedOrderDetail.setSubtotal(200.0);
        existedOrderDetail.setTotalAmount(200.0);
        existedOrderDetail.setDiscount(5.0f);

        // Mock dữ liệu từ repository
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer testToken");
        when(jwtTokenProvider.getUsernameFromToken("testToken")).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(new User());

        when(orderRepository.findByOrderId(orderId)).thenReturn(existingOrder);
        when(orderDetailRepository.findByOrderId(orderId)).thenReturn(List.of(existedOrderDetail));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        // Gọi phương thức updateOrder()
        Order result = orderService.updateOrder(orderId, createOrderDto, mockRequest);

        // Kiểm tra OrderDetails đã bị xóa
        verify(orderDetailRepository, times(1)).deleteAll(argThat(deletedOrderDetails ->
                StreamSupport.stream(deletedOrderDetails.spliterator(), false)
                        .anyMatch(detail -> detail.getProduct().getProductId() == 1)
        ));
    }
}