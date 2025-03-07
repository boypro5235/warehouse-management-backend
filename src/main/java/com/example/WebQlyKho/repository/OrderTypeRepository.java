package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.entity.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTypeRepository extends JpaRepository<OrderType, Integer> {
}
