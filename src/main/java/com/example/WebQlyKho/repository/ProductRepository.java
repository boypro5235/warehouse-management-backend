package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.Category;
import com.example.WebQlyKho.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    @Query(nativeQuery = true, value= "SELECT * FROM product p WHERE LOWER(p.product_name) LIKE LOWER(CONCAT('%', :searchText, '%'))" )
    List<Product> searchProductsByName(@Param("searchText") String searchText);

    List<Product> findByCategory(Category category);
}
