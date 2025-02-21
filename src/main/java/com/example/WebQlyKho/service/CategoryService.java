package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.CreateCategoryDto;
import com.example.WebQlyKho.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    Map<String, Object> searchCategories(String searchText, int page, int size);

    Category createCategory(CreateCategoryDto createCategoryDto);

    Category updateCategory(Integer categoryId, CreateCategoryDto createCategoryDto);

    Category getCategoryById(Integer categoryId);

    void deleteCategoriesByIds(List<Integer> ids);
}
