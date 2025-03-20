package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.CreateProductDto;
import com.example.WebQlyKho.entity.Category;
import com.example.WebQlyKho.entity.Product;
import com.example.WebQlyKho.exception.CategoryNotFoundException;
import com.example.WebQlyKho.repository.CategoryRepository;
import com.example.WebQlyKho.repository.ProductRepository;
import com.example.WebQlyKho.repository.StockRepository;
import com.example.WebQlyKho.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StockRepository stockRepository;

    @Override
    public List<Product> getProductByCategoryId(Integer categoryId) {
        // Tìm category dựa trên ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Không tồn tại category với id = " + categoryId));

        // Lấy danh sách product thuộc category đó
        return productRepository.findByCategory(category);
    }

    @Override
    public Map<String, Object> searchProducts(String searchText, Integer categoryId, int page, int size) {
        try {
            if (page > 0) {
                page = page - 1;
            }
            Pageable pageable = PageRequest.of(page, size);
            Specification<Product> specification = new Specification<Product>() {
                @Override
                public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    // Add search by name
                    predicates.add(criteriaBuilder.like(root.get("productName"), "%" + searchText + "%"));
                    // Filter by category ID
                    if (categoryId != null) {
                        predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"), categoryId));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<Product> pageProduct = productRepository.findAll(specification, pageable);
            List<Map<String, Object>> productList = new ArrayList<>();
            for (Product product : pageProduct.getContent()) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("product", product);
                productData.put("currentStock", stockRepository.calculateStock(product.getProductId()));
                productList.add(productData);
            }

            Map<String, Object> mapProduct = new HashMap<>();
            mapProduct.put("listProduct", productList);
            mapProduct.put("pageSize", pageProduct.getSize());
            mapProduct.put("pageNo", pageProduct.getNumber() + 1);
            mapProduct.put("totalPage", pageProduct.getTotalPages());
            return mapProduct;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Product createProduct(CreateProductDto createProductDto) {
        Product product = new Product();
        Category category = categoryRepository.findById(Integer.valueOf(createProductDto.getCategoryId()))
                .orElseThrow(() -> new CategoryNotFoundException("Không tồn tại category với id = " + createProductDto.getCategoryId()));
        product.setCategory(category);
        product.setProductName(createProductDto.getProductName());
        product.setDescription(createProductDto.getDescription());
        product.setPrice(createProductDto.getPrice());
        product.setStatus(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Integer productId, CreateProductDto createProductDto) {
        try {
            Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found for id: " + productId));
            Category category = categoryRepository.findById(Integer.valueOf(createProductDto.getCategoryId())).orElseThrow(() -> new EntityNotFoundException("Không tồn tại category với id = " + createProductDto.getCategoryId()));
            product.setCategory(category);
            product.setProductName(createProductDto.getProductName());
            product.setDescription(createProductDto.getDescription());
            product.setPrice(createProductDto.getPrice());
            product.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error updating product", e);
            return null;
        }
    }

    //Get Detail Department
    @Override
    public Product getProductById(Integer productId) {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Không tồn tại product với id = " + productId));
    }

    //Delete Department
    @Override
    public void deleteProductsByIds(List<Integer> ids) {
        List<Product> productsToDelete = productRepository.findAllById(ids);

        List<Integer> existingIds = productsToDelete.stream()
                .map(Product::getProductId)
                .toList();

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new EntityNotFoundException("Products not found for ids: " + notFoundIds);
        }

        productsToDelete.forEach(supplier -> supplier.setStatus(false));
        productRepository.saveAll(productsToDelete);
    }
}
