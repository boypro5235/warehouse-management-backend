package com.example.WebQlyKho.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "import_details")
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(ImportDetailsIds.class)
public class ImportDetails {
    @Id
    @ManyToOne
    @JoinColumn(name = "invoices_id")
    private ImportInvoice importInvoice;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

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
