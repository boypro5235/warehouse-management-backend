package com.example.WebQlyKho.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jdk.jfr.Registered;
import org.springframework.stereotype.Repository;

@Repository
public class StockRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public int calculateStock(int productId) {
        Query query = entityManager.createNativeQuery("select qlkhohang.calculatestock(:productid)");
        query.setParameter("productid", productId);

        return (int) query.getSingleResult();
    }

}
