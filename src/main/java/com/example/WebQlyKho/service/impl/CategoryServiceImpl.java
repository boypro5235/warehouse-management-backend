package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.CreateCategoryDto;
import com.example.WebQlyKho.entity.Category;
import com.example.WebQlyKho.exception.CategoryNotFoundException;
import com.example.WebQlyKho.repository.CategoryRepository;
import com.example.WebQlyKho.service.CategoryService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Map<String, Object> searchCategories(String searchText, int page, int size) {
        try {
            if (page > 0) {
                page = page - 1;
            }
            Pageable pageable = PageRequest.of(page, size);
            Specification<Category> specification = new Specification<Category>() {
                @Override
                public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    // Add search by name
                    predicates.add(criteriaBuilder.like(root.get("categoryName"), "%" + searchText + "%"));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<Category> pageCategory = categoryRepository.findAll(specification, pageable);
            Map<String, Object> mapCategory = new HashMap<>();
            mapCategory.put("listCategory", pageCategory.getContent());
            mapCategory.put("pageSize", pageCategory.getSize());
            mapCategory.put("pageNo", pageCategory.getNumber() + 1);
            mapCategory.put("totalPage", pageCategory.getTotalPages());
            return mapCategory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Category createCategory(CreateCategoryDto createCategoryDto) {
        try{
            Category category = new Category();
            category.setCategoryName(createCategoryDto.getCategoryName());
            return categoryRepository.save(category);
        } catch (Exception e) {
            log.error("Error creating category", e);
            return null;
        }
    }

    @Override
    public Category updateCategory(Integer categoryId, CreateCategoryDto createCategoryDto) {
        try {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found for id: " + categoryId));
            category.setCategoryName(createCategoryDto.getCategoryName());
            category.setUpdatedAt(LocalDateTime.now());
            return categoryRepository.save(category);
        } catch (Exception e) {
            log.error("Error updating category", e);
            return null;
        }
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found for id: " + categoryId));
    }

    @Override
    public void deleteCategoriesByIds(List<Integer> ids) {
        List<Category> categoriesToDelete = categoryRepository.findAllById(ids);

        List<Integer> existingIds = categoriesToDelete.stream()
                .map(Category::getCategoryId)
                .toList();

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new CategoryNotFoundException("Categories not found for ids: " + notFoundIds);
        }

        categoriesToDelete.forEach(category -> category.setStatus(false));
        categoryRepository.saveAll(categoriesToDelete);
    }
}
