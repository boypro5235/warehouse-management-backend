package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.CreateCategoryDto;
import com.example.WebQlyKho.dto.request.CreateSupplierDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.Category;
import com.example.WebQlyKho.entity.Supplier;
import com.example.WebQlyKho.exception.CategoryNotFoundException;
import com.example.WebQlyKho.exception.SupplierNotFoundException;
import com.example.WebQlyKho.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/categories")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody @Valid CreateCategoryDto createCategoryDto, BindingResult bindingResult) {
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
            Category category = categoryService.createCategory(createCategoryDto);
            return APIResponse.responseBuilder(category, "Category created successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating categories", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Object> searchCategories(@RequestParam(defaultValue = "")  String searchText,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            if(pageNo<=0&&pageSize<=0) {
                pageNo = 1;
                pageSize = 1;
            }
            Map<String, Object> mapCategory = categoryService.searchCategories(searchText, pageNo, pageSize);
            return APIResponse.responseBuilder(mapCategory, null, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting categories", e);
            return APIResponse.responseBuilder(null,"Error occurred while getting categories", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Integer categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            return APIResponse.responseBuilder(category, "Category with id= "+categoryId+" return successfully", HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error getting category", e);
            return APIResponse.responseBuilder(null, "Error occurred while getting category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Object> updateCategory(@PathVariable Integer categoryId, @RequestBody @Valid CreateCategoryDto createCategoryDto, BindingResult bindingResult) {
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
            Category category = categoryService.updateCategory(categoryId, createCategoryDto);
            return APIResponse.responseBuilder(category, "Category updated successfully", HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating category", e);
            return APIResponse.responseBuilder(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteCategories(@RequestBody DeleteRequest request) {
        try {
            if (request.getIds() == null || request.getIds().isEmpty()) {
                return APIResponse.responseBuilder(null, "The data sent is not in the correct format.", HttpStatus.BAD_REQUEST);
            }

            categoryService.deleteCategoriesByIds(request.getIds());
            return APIResponse.responseBuilder(
                    null,
                    "Categories deleted successfully",
                    HttpStatus.OK
            );
        } catch (CategoryNotFoundException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }catch (Exception e) {
            log.error("Unexpected error during deleting update", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred while deleting the categories",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
