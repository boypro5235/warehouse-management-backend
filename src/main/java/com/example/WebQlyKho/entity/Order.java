package com.example.WebQlyKho.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "orders")
@Getter
@Setter
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_type")
    private Integer orderType;

    @Column(name = "status")
    private boolean status;

    @Column(name = "customer")
    private String customer;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "khohang_id", nullable = false)
    private Dskhohang dskhohang;

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

    public Order() {

    }
}
