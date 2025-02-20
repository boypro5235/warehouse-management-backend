package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
    @Query(nativeQuery = true, value= "SELECT * FROM category c WHERE LOWER(c.category_name) LIKE LOWER(CONCAT('%', :searchText, '%')) AND c.status = true")
    List<Category> searchCategoriesByName(@Param("searchText") String searchText);
}