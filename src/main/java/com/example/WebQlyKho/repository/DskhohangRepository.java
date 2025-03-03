package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.Dskhohang;
import com.example.WebQlyKho.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DskhohangRepository extends JpaRepository<Dskhohang, Integer>, JpaSpecificationExecutor<Dskhohang> {
    @Query(nativeQuery = true, value= "SELECT * FROM dskhohang s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchText, '%')) AND s.status = true")
    List<Dskhohang> searchDskhohangsByName(@Param("searchText") String searchText);
}
