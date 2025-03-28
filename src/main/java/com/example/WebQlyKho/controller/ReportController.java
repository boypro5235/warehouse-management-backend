package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.response.ProductStock;
import com.example.WebQlyKho.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService stockService;

    @GetMapping("/getAllStock")
    public List<ProductStock> getAllStock() {
        return stockService.getAllStock();
    }
}
