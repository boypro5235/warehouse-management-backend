package com.example.WebQlyKho.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_type_id")
    private String orderType;

    @Column(name = "status")
    private String status;

    @Column(name = "customer")
    private String customer;

    @Column(name = "address")
    private String address;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "vat")
    private float vat;

    @Column(name = "discount")
    private float discount;

    @Column(name = "final_amount")
    private float finalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
