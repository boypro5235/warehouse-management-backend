package com.example.WebQlyKho.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "import_details")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImportDetails implements Serializable {
    @EmbeddedId
    private ImportDetailsIds id;

    @ManyToOne
    @JoinColumn(name = "invoices_id", nullable = false)
    private ImportInvoice importInvoice;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
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
