package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.dto.response.ProductStock;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public int calculateStock(int productId) {
        Query query = entityManager.createNativeQuery("select qlkhohang.calculatestock(:productid)");
        query.setParameter("productid", productId);

        return (int) query.getSingleResult();
    }

    public List<ProductStock> getAllStock() {
        Query query = entityManager.createNativeQuery("SELECT * FROM qlkhohang.getallstock()");

        List<Object[]> results = query.getResultList();

        return results.stream().map(row -> new ProductStock(
                (Integer) row[0],   // product_id
                (String) row[1],    // product_name
                (Integer) row[2]    // stock
        )).toList();
    }
}
