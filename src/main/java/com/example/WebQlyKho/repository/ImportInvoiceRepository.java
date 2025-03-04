package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.ImportInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportInvoiceRepository extends JpaRepository<ImportInvoice,Integer>, JpaSpecificationExecutor<ImportInvoice> {
}
