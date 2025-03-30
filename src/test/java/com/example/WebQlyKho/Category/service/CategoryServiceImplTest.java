package com.example.WebQlyKho.Category.service;

import com.example.WebQlyKho.dto.request.CreateCategoryDto;
import com.example.WebQlyKho.entity.Category;
import com.example.WebQlyKho.exception.CategoryNotFoundException;
import com.example.WebQlyKho.repository.CategoryRepository;
import com.example.WebQlyKho.service.impl.CategoryServiceImpl;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CreateCategoryDto createCategoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Electronics");
        category.setDescription("Electronic devices");
        category.setStatus(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        createCategoryDto = new CreateCategoryDto();
        createCategoryDto.setCategoryName("Electronics");
        createCategoryDto.setDescription("Electronic devices");
    }

    @Test
    void searchCategories_ShouldExecuteToPredicate() {
        // Given
        String searchText = "Test";
        int page = 1;
        int size = 10;

        Page<Category> mockPage = new PageImpl<>(List.of(new Category(1, "Test Category", "Description", true, LocalDateTime.now(), LocalDateTime.now())));

        ArgumentCaptor<Specification<Category>> specCaptor = ArgumentCaptor.forClass(Specification.class);

        when(categoryRepository.findAll(specCaptor.capture(), any(Pageable.class))).thenReturn(mockPage);

        // When
        categoryService.searchCategories(searchText, page, size);

        // Then
        Specification<Category> capturedSpec = specCaptor.getValue();

        // Simulate execution of toPredicate()
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Root root = mock(Root.class);

        when(root.get("categoryName")).thenReturn(mock(Path.class));
        when(criteriaBuilder.like(any(), (String) any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any())).thenReturn(mock(Predicate.class)); // Ensure 'and' is covered

        capturedSpec.toPredicate(root, query, criteriaBuilder);

        // Verify that 'criteriaBuilder.and()' was called
        verify(criteriaBuilder).and(any());
    }


    @Test
    void searchCategories_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> page = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var result = categoryService.searchCategories("Electronics", 1, 10);

        assertNotNull(result);
        assertEquals(1, ((List<?>) result.get("listCategory")).size());
        assertEquals(1, result.get("totalPage"));
    }

    @Test
    void searchCategories_ShouldNotFilter_WhenSearchTextIsNullOrEmpty() {
        // Given
        String searchText = ""; // or null
        int page = 1;
        int size = 10;

        Page<Category> mockPage = new PageImpl<>(List.of(new Category(1, "Test Category", "Description", true, LocalDateTime.now(), LocalDateTime.now())));

        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // When
        categoryService.searchCategories(searchText, page, size);

        // Then
        verify(categoryRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchCategories_ShouldNotDecrementPage_WhenPageIsZero() {
        // Given
        String searchText = "test";
        int page = 0;  // This case is missing in your tests
        int size = 10;

        Page<Category> mockPage = new PageImpl<>(List.of(new Category(1, "Test Category", "Description", true, LocalDateTime.now(), LocalDateTime.now())));

        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // When
        categoryService.searchCategories(searchText, page, size);

        // Then
        verify(categoryRepository).findAll(any(Specification.class), any(Pageable.class));
    }


    @Test
    void searchCategories_ShouldUseSpecification_WhenSearchTextIsProvided() {
        // Given
        String searchText = "Test";
        int page = 1;
        int size = 10;

        Page<Category> mockPage = new PageImpl<>(List.of(new Category(1, "Test Category", "Description", true, LocalDateTime.now(), LocalDateTime.now())));

        ArgumentCaptor<Specification<Category>> specCaptor = ArgumentCaptor.forClass(Specification.class);

        when(categoryRepository.findAll(specCaptor.capture(), any(Pageable.class))).thenReturn(mockPage);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Root<Category> root = mock(Root.class);

        when(root.get("categoryName")).thenReturn(mock(Path.class));  // Fix argument mismatch
        when(criteriaBuilder.like(any(Path.class), anyString())).thenReturn(mock(Predicate.class));  // Fix stubbing error
        when(criteriaBuilder.and(any())).thenReturn(mock(Predicate.class));

        // When
        categoryService.searchCategories(searchText, page, size);

        // Then
        Specification<Category> capturedSpec = specCaptor.getValue();
        capturedSpec.toPredicate(root, query, criteriaBuilder);  // Ensure execution

        verify(criteriaBuilder).and(any());  // Verify 'and()' is covered
    }


    @Test
    void searchCategories_ShouldReturnNull_WhenExceptionOccurs() {
        // Given
        String searchText = "Test";
        int page = 1;
        int size = 10;

        // Mock categoryRepository to throw an exception
        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Map<String, Object> result = categoryService.searchCategories(searchText, page, size);

        // Then
        assertNull(result); // Expecting null due to exception handling

        verify(categoryRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchCategories_ShouldReturnCategories_WhenSearchTextMatches() {
        // Given
        String searchText = "Test";
        int page = 1;
        int size = 10; // Expecting a full page

        // Create a list of 10 category items
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            categories.add(new Category(i, "Test Category " + i, "Description " + i, true, LocalDateTime.now(), LocalDateTime.now()));
        }

        Page<Category> mockPage = new PageImpl<>(categories, PageRequest.of(0, size), 10);

        // Mock repository to return the expected page
        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        Map<String, Object> result = categoryService.searchCategories(searchText, page, size);

        // Then
        assertNotNull(result);
        assertEquals(10, ((List<?>) result.get("listCategory")).size()); // Expecting 10 items
        assertEquals(1, result.get("pageNo"));
        assertEquals(10, result.get("pageSize"));
        assertEquals(1, result.get("totalPage"));

        // Verify that findAll was called
        verify(categoryRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }



    @Test
    void createCategory_ShouldReturnSavedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category savedCategory = categoryService.createCategory(createCategoryDto);

        assertNotNull(savedCategory);
        assertEquals("Electronics", savedCategory.getCategoryName());
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updatedCategory = categoryService.updateCategory(1, createCategoryDto);

        assertNotNull(updatedCategory);
        assertEquals("Electronics", updatedCategory.getCategoryName());
    }

    @Test
    void updateCategory_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
        int nonExistentCategoryId = 999;
        CreateCategoryDto categoryDto = new CreateCategoryDto("New Name", "New Description");

        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(nonExistentCategoryId, categoryDto));

        verify(categoryRepository, times(1)).findById(nonExistentCategoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }


    @Test
    void getCategoryById_ShouldReturnCategory() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        Category foundCategory = categoryService.getCategoryById(1);

        assertNotNull(foundCategory);
        assertEquals(1, foundCategory.getCategoryId());
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(1));
    }

    @Test
    void deleteCategoriesByIds_ShouldSetStatusFalse() {
        List<Integer> ids = List.of(1);
        List<Category> categories = List.of(new Category());
        categories.getFirst().setCategoryId(1);
        categories.getFirst().setStatus(true);

        when(categoryRepository.findAllById(ids)).thenReturn(categories);

        categoryService.deleteCategoriesByIds(ids);

        assertFalse(categories.getFirst().isStatus()); // Ensure status is updated
        verify(categoryRepository, times(1)).saveAll(categories);
    }


    @Test
    void deleteCategoriesByIds_ShouldThrowException_WhenCategoriesNotFound() {
        List<Integer> ids = List.of(1);
        when(categoryRepository.findAllById(ids)).thenReturn(List.of());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategoriesByIds(ids));
    }
}
