package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Category;
import cz.upce.fei.redsys.dto.CategoryDto.CategoryResponse;
import cz.upce.fei.redsys.dto.CategoryDto.CreateCategoryRequest;
import cz.upce.fei.redsys.dto.CategoryDto.PaginatedCategoryResponse;
import cz.upce.fei.redsys.dto.CategoryDto.UpdateCategoryRequest;
import cz.upce.fei.redsys.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Technology";
    private static final String CATEGORY_DESC = "Tech related articles";

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(CATEGORY_NAME);
        category.setDescription(CATEGORY_DESC);
    }

    @Test
    void create_ShouldSaveCategoryAndReturnResponse() {
        CreateCategoryRequest request =
                new CreateCategoryRequest(CATEGORY_NAME, CATEGORY_DESC);

        when(categoryRepository.existsByName(CATEGORY_NAME)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(CATEGORY_ID);
            return c;
        });

        CategoryResponse response = categoryService.create(request);

        assertNotNull(response);
        assertEquals(CATEGORY_ID, response.id());
        assertEquals(CATEGORY_NAME, response.name());
        assertEquals(CATEGORY_DESC, response.description());
        verify(categoryRepository, times(1)).existsByName(CATEGORY_NAME);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void create_ShouldThrowIllegalStateException_WhenNameAlreadyExists() {
        CreateCategoryRequest request =
                new CreateCategoryRequest(CATEGORY_NAME, CATEGORY_DESC);

        when(categoryRepository.existsByName(CATEGORY_NAME)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> categoryService.create(request));

        verify(categoryRepository, times(1)).existsByName(CATEGORY_NAME);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void get_ShouldReturnCategoryResponse_WhenCategoryExists() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.get(CATEGORY_ID);

        assertNotNull(response);
        assertEquals(CATEGORY_ID, response.id());
        assertEquals(CATEGORY_NAME, response.name());
        assertEquals(CATEGORY_DESC, response.description());
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
    }

    @Test
    void get_ShouldThrowEntityNotFoundException_WhenCategoryNotFound() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.get(CATEGORY_ID));

        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
    }

    @Test
    void list_ShouldReturnPaginatedCategories() {
        Pageable pageable = PageRequest.of(0, 20);
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Science");
        category2.setDescription("Science related");

        Page<Category> page = new PageImpl<>(List.of(category, category2), pageable, 2);

        when(categoryRepository.findAll(pageable)).thenReturn(page);

        PaginatedCategoryResponse response = categoryService.list(pageable);

        assertNotNull(response);
        assertEquals(2, response.categories().size());
        assertEquals(0, response.page());
        assertEquals(20, response.size());
        assertEquals(2, response.totalElements());
        assertEquals(1, response.totalPages());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void list_ShouldReturnEmptyList_WhenNoCategories() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Category> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

        PaginatedCategoryResponse response = categoryService.list(pageable);

        assertNotNull(response);
        assertEquals(0, response.categories().size());
        assertEquals(0, response.totalElements());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void update_ShouldUpdateCategoryAndReturnResponse() {
        UpdateCategoryRequest request =
                new UpdateCategoryRequest("New name", "New description");

        Category updated = new Category();
        updated.setId(CATEGORY_ID);
        updated.setName("New name");
        updated.setDescription("New description");

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("New name")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updated);

        CategoryResponse response = categoryService.update(CATEGORY_ID, request);

        assertNotNull(response);
        assertEquals(CATEGORY_ID, response.id());
        assertEquals("New name", response.name());
        assertEquals("New description", response.description());
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryRepository, times(1)).existsByName("New name");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenCategoryNotFound() {
        UpdateCategoryRequest request =
                new UpdateCategoryRequest("New name", "New description");

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(CATEGORY_ID, request));

        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowIllegalStateException_WhenNewNameAlreadyExists() {
        UpdateCategoryRequest request =
                new UpdateCategoryRequest("Existing name", "Desc");

        Category existing = new Category();
        existing.setId(CATEGORY_ID);
        existing.setName("Old name");
        existing.setDescription("Old desc");

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("Existing name")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> categoryService.update(CATEGORY_ID, request));

        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryRepository, times(1)).existsByName("Existing name");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_ShouldNotCheckExistsByName_WhenNameUnchanged() {
        UpdateCategoryRequest request =
                new UpdateCategoryRequest(CATEGORY_NAME, "New description");

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.update(CATEGORY_ID, request);

        assertNotNull(response);
        assertEquals(CATEGORY_NAME, response.name());
        assertEquals("New description", response.description());
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryRepository, never()).existsByName(anyString());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void delete_ShouldDeleteCategory_WhenExists() {
        when(categoryRepository.existsById(CATEGORY_ID)).thenReturn(true);

        assertDoesNotThrow(() -> categoryService.delete(CATEGORY_ID));

        verify(categoryRepository, times(1)).existsById(CATEGORY_ID);
        verify(categoryRepository, times(1)).deleteById(CATEGORY_ID);
    }

    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenCategoryNotFound() {
        when(categoryRepository.existsById(CATEGORY_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.delete(CATEGORY_ID));

        verify(categoryRepository, times(1)).existsById(CATEGORY_ID);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void findAllByIds_ShouldReturnCategories() {
        Set<Long> ids = Set.of(1L, 2L);
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Science");

        when(categoryRepository.findAllById(ids))
                .thenReturn(List.of(category, category2));

        List<Category> result = categoryService.findAllByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAllById(ids);
    }
}
