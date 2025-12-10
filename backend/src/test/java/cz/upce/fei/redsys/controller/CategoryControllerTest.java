package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.fei.redsys.dto.CategoryDto.*;
import cz.upce.fei.redsys.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class CategoryControllerTest {

    private static final String API_BASE = "/api/categories";
    private static final String TEST_NAME = "Technology";
    private static final String TEST_DESCRIPTION = "Technology related articles";
    private static final Long TEST_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void create_ShouldReturnCategoryAnd201() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest(TEST_NAME, TEST_DESCRIPTION);
        CategoryResponse mockResponse = CategoryResponse.builder()
                .id(TEST_ID)
                .name(TEST_NAME)
                .description(TEST_DESCRIPTION)
                .build();

        when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post(API_BASE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));
    }

    @Test
    @WithMockUser(username = "user")
    void get_ShouldReturnCategoryAnd200() throws Exception {
        CategoryResponse mockResponse = CategoryResponse.builder()
                .id(TEST_ID)
                .name(TEST_NAME)
                .description(TEST_DESCRIPTION)
                .build();

        when(categoryService.get(TEST_ID)).thenReturn(mockResponse);

        mockMvc.perform(get(API_BASE + "/{id}", TEST_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));
    }

    @Test
    @WithMockUser(username = "user")
    void list_ShouldReturnPaginatedCategoriesAnd200() throws Exception {
        CategoryResponse category1 = CategoryResponse.builder()
                .id(1L)
                .name("Technology")
                .description("Tech articles")
                .build();

        CategoryResponse category2 = CategoryResponse.builder()
                .id(2L)
                .name("Sports")
                .description("Sports articles")
                .build();

        PaginatedCategoryResponse mockResponse = new PaginatedCategoryResponse(
                List.of(category1, category2),
                0,
                20,
                2L,
                1
        );

        when(categoryService.list(any(Pageable.class))).thenReturn(mockResponse);

        mockMvc.perform(get(API_BASE)
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(2))
                .andExpect(jsonPath("$.categories[0].name").value("Technology"))
                .andExpect(jsonPath("$.categories[1].name").value("Sports"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_ShouldReturnUpdatedCategoryAnd200() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest(
                "Updated Technology",
                "Updated description"
        );

        CategoryResponse mockResponse = CategoryResponse.builder()
                .id(TEST_ID)
                .name("Updated Technology")
                .description("Updated description")
                .build();

        when(categoryService.update(eq(TEST_ID), any(UpdateCategoryRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put(API_BASE + "/{id}", TEST_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value("Updated Technology"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(categoryService).delete(TEST_ID);

        mockMvc.perform(delete(API_BASE + "/{id}", TEST_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void get_ShouldReturn404WhenCategoryNotFound() throws Exception {
        when(categoryService.get(TEST_ID))
                .thenThrow(new EntityNotFoundException("Category not found"));

        mockMvc.perform(get(API_BASE + "/{id}", TEST_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete_ShouldReturn404WhenCategoryNotFound() throws Exception {
        Long nonExistentId = 999L;

        doThrow(new EntityNotFoundException("Category not found"))
                .when(categoryService).delete(nonExistentId);

        mockMvc.perform(delete(API_BASE + "/{id}", nonExistentId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void list_EmptyResult_ShouldReturnEmptyArrayAnd200() throws Exception {
        PaginatedCategoryResponse mockResponse = new PaginatedCategoryResponse(
                List.of(),
                0,
                20,
                0L,
                0
        );

        when(categoryService.list(any(Pageable.class))).thenReturn(mockResponse);

        mockMvc.perform(get(API_BASE)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}