package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer>, JpaSpecificationExecutor<Supplier> {
    @Query(nativeQuery = true, value= "SELECT * FROM supplier s WHERE LOWER(s.supplier_name) LIKE LOWER(CONCAT('%', :searchText, '%')) AND s.status = true")
    List<Supplier> searchSuppliersByName(@Param("searchText") String searchText);
}
