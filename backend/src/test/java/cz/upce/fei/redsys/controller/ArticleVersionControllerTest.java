package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleVersion;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.dto.ArticleVersionDto.ArticleVersionResponse;
import cz.upce.fei.redsys.dto.ArticleVersionDto.PaginatedArticleVersionResponse;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.service.ArticleService;
import cz.upce.fei.redsys.service.ArticleVersionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ArticleVersionControllerTest {

    private static final Long TEST_ARTICLE_ID = 1L;
    private static final Integer TEST_VERSION_NUMBER = 1;
    private static final String TEST_CONTENT = "Version 1 content";
    private static final String TEST_USERNAME = "testUser";
    private static final Instant TEST_CREATED_AT = Instant.parse("2024-12-10T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @MockitoBean
    private ArticleVersionService articleVersionService;

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void list_ShouldReturnPaginatedVersionsAnd200() throws Exception {
        // Mock article
        Article mockArticle = mock(Article.class);
        when(articleService.requireArticleById(TEST_ARTICLE_ID)).thenReturn(mockArticle);

        // Mock versions (without content)
        UserResponse creator1 = new UserResponse(1L, "creator1", "Creator One", UserRole.EDITOR);
        UserResponse creator2 = new UserResponse(2L, "creator2", "Creator Two", UserRole.EDITOR);

        ArticleVersionResponse version1 = ArticleVersionResponse.builder()
                .id(1L)
                .versionNumber(1)
                .createdAt(TEST_CREATED_AT)
                .createdBy(creator1)
                .content(null)  // No content in list
                .build();

        ArticleVersionResponse version2 = ArticleVersionResponse.builder()
                .id(2L)
                .versionNumber(2)
                .createdAt(TEST_CREATED_AT.plusSeconds(3600))
                .createdBy(creator2)
                .content(null)  // No content in list
                .build();

        PaginatedArticleVersionResponse mockResponse = new PaginatedArticleVersionResponse(
                List.of(version1, version2),
                0,
                20,
                2L,
                1
        );

        when(articleVersionService.listVersions(eq(mockArticle), any(Pageable.class)))
                .thenReturn(mockResponse);

        // Perform request
        mockMvc.perform(get("/api/articles/{articleId}/versions", TEST_ARTICLE_ID)
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versions").isArray())
                .andExpect(jsonPath("$.versions.length()").value(2))
                .andExpect(jsonPath("$.versions[0].id").value(1L))
                .andExpect(jsonPath("$.versions[0].versionNumber").value(1))
                .andExpect(jsonPath("$.versions[0].createdBy.username").value("creator1"))
                .andExpect(jsonPath("$.versions[0].content").doesNotExist())
                .andExpect(jsonPath("$.versions[1].versionNumber").value(2))
                .andExpect(jsonPath("$.versions[1].createdBy.username").value("creator2"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void get_ShouldReturnVersionWithContentAnd200() throws Exception {
        Article mockArticle = mock(Article.class);
        when(articleService.requireArticleById(TEST_ARTICLE_ID)).thenReturn(mockArticle);

        ArticleVersion mockVersion = mock(ArticleVersion.class);
        when(articleVersionService.getVersion(mockArticle, TEST_VERSION_NUMBER))
                .thenReturn(mockVersion);

       new UserResponse(1L, "creator", "Creator Name", UserRole.EDITOR);

        when(mockVersion.getId()).thenReturn(1L);
        when(mockVersion.getVersionNumber()).thenReturn(TEST_VERSION_NUMBER);
        when(mockVersion.getCreatedAt()).thenReturn(TEST_CREATED_AT);
        when(mockVersion.getContent()).thenReturn(TEST_CONTENT);

        // Perform request
        mockMvc.perform(get("/api/articles/{articleId}/versions/{versionNumber}",
                        TEST_ARTICLE_ID, TEST_VERSION_NUMBER)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.versionNumber").value(TEST_VERSION_NUMBER))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void list_ShouldReturn404WhenArticleNotFound() throws Exception {
        // Mock article service to throw EntityNotFoundException
        when(articleService.requireArticleById(TEST_ARTICLE_ID))
                .thenThrow(new EntityNotFoundException("Article not found"));

        mockMvc.perform(get("/api/articles/{articleId}/versions", TEST_ARTICLE_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void get_ShouldReturn404WhenVersionNotFound() throws Exception {
        // Mock article
        Article mockArticle = mock(Article.class);
        when(articleService.requireArticleById(TEST_ARTICLE_ID)).thenReturn(mockArticle);

        // Mock version service to throw EntityNotFoundException
        when(articleVersionService.getVersion(mockArticle, TEST_VERSION_NUMBER))
                .thenThrow(new EntityNotFoundException("Version not found"));

        mockMvc.perform(get("/api/articles/{articleId}/versions/{versionNumber}",
                        TEST_ARTICLE_ID, TEST_VERSION_NUMBER)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void list_WithCustomPageSize_ShouldReturnCorrectPagination() throws Exception {
        // Mock article
        Article mockArticle = mock(Article.class);
        when(articleService.requireArticleById(TEST_ARTICLE_ID)).thenReturn(mockArticle);

        // Mock single version
        UserResponse creator = new UserResponse(1L, "creator", "Creator", UserRole.EDITOR);

        ArticleVersionResponse version = ArticleVersionResponse.builder()
                .id(1L)
                .versionNumber(1)
                .createdAt(TEST_CREATED_AT)
                .createdBy(creator)
                .content(null)
                .build();

        PaginatedArticleVersionResponse mockResponse = new PaginatedArticleVersionResponse(
                List.of(version),
                0,
                5,  // Custom page size
                1L,
                1
        );

        when(articleVersionService.listVersions(eq(mockArticle), any(Pageable.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/articles/{articleId}/versions", TEST_ARTICLE_ID)
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.versions.length()").value(1));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void list_EmptyResult_ShouldReturnEmptyArrayAnd200() throws Exception {
        // Mock article
        Article mockArticle = mock(Article.class);
        when(articleService.requireArticleById(TEST_ARTICLE_ID)).thenReturn(mockArticle);

        // Mock empty response
        PaginatedArticleVersionResponse mockResponse = new PaginatedArticleVersionResponse(
                List.of(),
                0,
                20,
                0L,
                0
        );

        when(articleVersionService.listVersions(eq(mockArticle), any(Pageable.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/articles/{articleId}/versions", TEST_ARTICLE_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versions").isArray())
                .andExpect(jsonPath("$.versions.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}