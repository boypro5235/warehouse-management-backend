package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.response.ProductStock;
import com.example.WebQlyKho.repository.StockRepository;
import com.example.WebQlyKho.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private StockRepository stockRepository;

    @Override
    public List<ProductStock> getAllStock() {
        return stockRepository.getAllStock();
    }
}
