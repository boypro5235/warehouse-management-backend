package com.example.WebQlyKho.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "import_invoices")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImportInvoice {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer invoicesId;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name="import_date", nullable = false)
    private LocalDate importDate;

    @Column(name="total_amount",nullable = false)
    private Double totalAmount;

    @Column(name="status")
    private Boolean status = true;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "importInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportDetail> importDetails;
}