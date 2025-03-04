package com.example.WebQlyKho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "order_details")
@IdClass(OrderDetailsIds.class)
public class OrderDetails{
    @ManyToOne
    @Id
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @Id
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private float price;

    @ManyToOne
    @JoinColumn(name = "khohang_id", nullable = false)
    private Dskhohang dskhohang;

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "vat")
    private float vat;

    @Column(name = "discount")
    private float discount;

    @Column(name = "total_amount")
    private double totalAmount;
}

