package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.CreateOrderDto;
import com.example.WebQlyKho.dto.request.OrderDetailsRequest;
import com.example.WebQlyKho.entity.Order;
import com.example.WebQlyKho.entity.OrderDetails;
import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.repository.*;
import com.example.WebQlyKho.service.OrderService;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceIplm implements OrderService {
    @Autowired
    private DskhohangRepository dskhohangRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private StockRepository stockRepository;

    @Override
    public List<Order> searchOrders(String orderStatus, Integer orderType, String customer, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (fromDate != null && !fromDate.isEmpty()) {
            fromDate = validateDate(fromDate, formatter);
        }
        if (toDate != null && !toDate.isEmpty()) {
            toDate = validateDate(toDate, formatter);
        }

        String finalFromDate = fromDate;
        String finalToDate = toDate;
        Specification<Order> specification = (Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orderStatus != null && !orderStatus.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
            }

            if (orderType != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderType"), orderType));
            }

            if (customer != null && !customer.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("customer"), "%" + customer + "%"));
            }

            if (finalFromDate != null && finalToDate != null) {
                predicates.add(criteriaBuilder.between(
                        root.get("createdAt"),
                        LocalDate.parse(finalFromDate).atStartOfDay(),
                        LocalDate.parse(finalToDate).atTime(23, 59, 59)
                ));
            } else if (finalFromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), LocalDate.parse(finalFromDate).atStartOfDay()));
            } else if (finalToDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), LocalDate.parse(finalToDate).atTime(23, 59, 59)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(specification);
    }

    @Override
    public Order updateOrder(Integer orderId, CreateOrderDto createOrderDto, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        User user = userRepository.findByUsername(jwtTokenProvider.getUsernameFromToken(request.getHeader("Authorization").substring(7)));
        if(createOrderDto.getOrderType() == 1){
            order.setCustomer(null);
            order.setAddress(null);
            order.setDskhohang(dskhohangRepository.findById(createOrderDto.getKhohangId())
                    .orElseThrow(() -> new RuntimeException("Kho hàng không tồn tại!")));
        } else if (createOrderDto.getOrderType() == 2){
            order.setCustomer(createOrderDto.getCustomer());
            order.setAddress(createOrderDto.getAddress());
            order.setDskhohang(null);
        }
        order.setOrderType(createOrderDto.getOrderType());
        order.setOrderStatus(createOrderDto.getStatus());
        order.setTotalAmount(createOrderDto.getTotalAmount());
        order.setVat(createOrderDto.getVat());
        order.setDiscount(createOrderDto.getDiscount());
        order.setFinalAmount(createOrderDto.getFinalAmount());
        order.setStatus(true);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        List<OrderDetails> existedOrderDetails = orderDetailRepository.findByOrderId(orderId);
        List<OrderDetailsRequest> upcomingOrderDetails = createOrderDto.getOrderDetails();
        List<OrderDetails> deletedOrderDetails = existedOrderDetails.stream()
                .filter(detail -> upcomingOrderDetails.stream()
                        .noneMatch(upcomingDetail -> detail.getProduct().getProductId() == upcomingDetail.getProductId()))
                .toList();
        List<OrderDetails> savedOrderDetails = new ArrayList<>();
        for (OrderDetailsRequest upcomingDetail : upcomingOrderDetails) {
            OrderDetails existedOrderDetail = existedOrderDetails.stream()
                    .filter(detail -> detail.getProduct().getProductId() == upcomingDetail.getProductId())
                    .findFirst()
                    .orElse(null);
//            int curentProductStock = stockRepository.calculateStock(upcomingDetail.getProductId());
//            if(curentProductStock + (existedOrderDetail != null ? existedOrderDetail.getQuantity() : 0) < upcomingDetail.getQuantity()){
//                throw new RuntimeException("Product out of stock! Current: " + curentProductStock + (existedOrderDetail != null ? existedOrderDetail.getQuantity() : 0));
//            }

            if (existedOrderDetail != null) {
                existedOrderDetail.setQuantity(upcomingDetail.getQuantity());
                existedOrderDetail.setSubtotal(upcomingDetail.getSubtotal());
                existedOrderDetail.setTotalAmount(upcomingDetail.getTotalAmount());
                existedOrderDetail.setDiscount(upcomingDetail.getDiscount());
                savedOrderDetails.add(existedOrderDetail);
            } else {
                OrderDetails newOrderDetail = new OrderDetails();
                newOrderDetail.setOrder(order);
                newOrderDetail.setProduct(productRepository.getReferenceById(upcomingDetail.getProductId()));
                newOrderDetail.setQuantity(upcomingDetail.getQuantity());
                newOrderDetail.setSubtotal(upcomingDetail.getSubtotal());
                newOrderDetail.setTotalAmount(upcomingDetail.getTotalAmount());
                newOrderDetail.setDiscount(upcomingDetail.getDiscount());
                savedOrderDetails.add(newOrderDetail);
            }
            orderDetailRepository.deleteAll(deletedOrderDetails);
            orderDetailRepository.saveAll(savedOrderDetails);
        }

        return null;
    }


    @Override
    public Order createOrder(CreateOrderDto createOrderDto, HttpServletRequest request) {
        User user = userRepository.findByUsername(request.getUserPrincipal().getName());
        Order order = new Order();
        if(createOrderDto.getOrderType() == 1){
            order.setCustomer(null);
            order.setAddress(null);
            order.setDskhohang(dskhohangRepository.findById(createOrderDto.getKhohangId())
                    .orElseThrow(() -> new RuntimeException("Kho hàng không tồn tại!")));
        } else if (createOrderDto.getOrderType() == 2){
            order.setCustomer(createOrderDto.getCustomer());
            order.setAddress(createOrderDto.getAddress());
            order.setDskhohang(null);
        }
        order.setUser(user);
        order.setOrderType(createOrderDto.getOrderType());
        order.setOrderStatus(createOrderDto.getStatus());
        order.setTotalAmount(createOrderDto.getTotalAmount());
        order.setVat(createOrderDto.getVat());
        order.setDiscount(createOrderDto.getDiscount());
        order.setFinalAmount(createOrderDto.getFinalAmount());
        order.setStatus(true);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        for (OrderDetailsRequest orderDetailDto : createOrderDto.getOrderDetails()) {
//            int currentStock = stockRepository.calculateStock(orderDetailDto.getProductId());
//
//            if(currentStock < orderDetailDto.getQuantity()){
//                throw new RuntimeException("Product out of stock! Current: " + currentStock);
//            }

            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(productRepository.getReferenceById(orderDetailDto.getProductId()));
            orderDetail.setQuantity(orderDetailDto.getQuantity());
            orderDetail.setSubtotal(orderDetailDto.getSubtotal());
            orderDetail.setTotalAmount(orderDetailDto.getTotalAmount());
            orderDetail.setDiscount(orderDetailDto.getDiscount());

            orderDetailRepository.save(orderDetail);
        }

        return savedOrder;
    }

    @Override
    public void deleteImportInvoice(List<Integer> ids) {
        List<Order> ordersDelete = orderRepository.findAllById(ids);

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> ordersDelete.stream().noneMatch(order -> order.getOrderId().equals(id)))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new EntityNotFoundException("Import invoices not found for ids: " + notFoundIds);
        }

        orderRepository.deleteAll(ordersDelete);
    }

    @Override
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    private String validateDate(String date, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(date, formatter).toString();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date + ". Please use yyyy-MM-dd", e);
        }
    }
}
