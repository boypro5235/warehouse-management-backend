package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.response.ProductStock;

import java.util.List;

public interface ReportService {
    List<ProductStock> getAllStock();
}
