package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.Dskhohang;
import com.example.WebQlyKho.entity.ImportDetailsIds;
import com.example.WebQlyKho.entity.ImportDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImportDetailsRepository extends JpaRepository<ImportDetails, ImportDetailsIds>, JpaSpecificationExecutor<ImportDetails> {
}
