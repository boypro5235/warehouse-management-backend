package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.CreateProductDto;
import com.example.WebQlyKho.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<String, Object> searchProducts(String searchText, Integer categoryId, int page, int size);
    Product createProduct(CreateProductDto createProductDto);
    Product updateProduct(Integer productId, CreateProductDto createProductDto);
    Product getProductById(Integer productId);
    void deleteProductsByIds(List<Integer> ids);
    List<Product> getProductByCategoryId(Integer categoryId);
}
