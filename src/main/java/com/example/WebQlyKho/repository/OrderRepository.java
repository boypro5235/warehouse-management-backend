package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> , JpaSpecificationExecutor<Order> {
    @Query(value = "SELECT * FROM qlkhohang.orders WHERE order_id = :orderId", nativeQuery = true)
    Order findByOrderId(@Param("orderId") Integer orderId);

}
