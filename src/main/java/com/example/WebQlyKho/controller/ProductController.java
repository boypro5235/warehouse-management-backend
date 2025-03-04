package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.CreateProductDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.Product;
import com.example.WebQlyKho.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/products")
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody @Valid CreateProductDto createProductDto, BindingResult bindingResult) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
            }
            if (!errors.isEmpty()) {
                return APIResponse.responseBuilder(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                );
            }
            Product product = productService.createProduct(createProductDto);
            return APIResponse.responseBuilder(product, "Product created successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            log.error("Error creating product", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping
    public ResponseEntity<Object> searchProducts(@RequestParam(defaultValue = "") String searchText,
                                                 @RequestParam Integer categoryId,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            if(pageNo<=0&&pageSize<=0) {
                pageNo = 1;
                pageSize = 1;
            }
            Map<String, Object> mapProduct = productService.searchProducts(searchText, categoryId, pageNo, pageSize);
            return APIResponse.responseBuilder(mapProduct, "List product", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error search product", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Object> getProductById(@PathVariable Integer productId) {
        try {
            Product product = productService.getProductById(productId);
            return APIResponse.responseBuilder(product, "Product with id= "+productId+" return successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error getting product", e);
            return APIResponse.responseBuilder(null, "Error occurred while getting product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Object> updateProduct(@PathVariable Integer productId, @RequestBody @Valid CreateProductDto createProductDto, BindingResult bindingResult) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
            }
            if (!errors.isEmpty()) {
                return APIResponse.responseBuilder(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                );
            }
            Product product = productService.updateProduct(productId, createProductDto);
            return APIResponse.responseBuilder(product, "Product updated successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating product", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteProductsByIds(@RequestBody DeleteRequest request) {
        try {
            if (request.getIds() == null || request.getIds().isEmpty()) {
                return APIResponse.responseBuilder(null, "The data sent is not in the correct format.", HttpStatus.BAD_REQUEST);
            }

            productService.deleteProductsByIds(request.getIds());
            return APIResponse.responseBuilder(
                    null,
                    "Products deleted successfully",
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }catch (Exception e) {
            log.error("Unexpected error during deleting update", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred while deleting the products",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
