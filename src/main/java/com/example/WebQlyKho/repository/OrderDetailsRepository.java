package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.OrderDetails;
import com.example.WebQlyKho.entity.OrderDetailsIds;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, OrderDetailsIds> {
    @Query(nativeQuery = true, value = "SELECT " +
            "(COALESCE(SUM(i.quantity), 0) - COALESCE(SUM(o.quantity), 0)) AS current_stock " +
            "FROM qlkhohang.products p " +
            "LEFT JOIN qlkhohang.import_details i ON p.product_id = i.product_id " +
            "LEFT JOIN qlkhohang.order_details o ON p.product_id = o.product_id " +
            "WHERE p.product_id = :productId " +
            "GROUP BY p.product_id")
    @Transactional
    Integer countQuantityByProductId(@Param("productId") Integer productId);

    @Query("SELECT od FROM OrderDetails od WHERE od.order.orderId = :orderId")
    List<OrderDetails> findByOrderId(@Param("orderId") Integer orderId);

}
