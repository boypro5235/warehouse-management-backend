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
public class ImportDetail {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "import_details_id")
    private Integer importDetailsId;

    @ManyToOne
    @JoinColumn(name = "invoices_id", nullable = false)
    private ImportInvoice importInvoice;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;
}
