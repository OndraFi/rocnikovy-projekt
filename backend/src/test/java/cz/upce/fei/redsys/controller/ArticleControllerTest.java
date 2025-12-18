package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.fei.redsys.domain.ArticleState;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.dto.ArticleDto.*;
import cz.upce.fei.redsys.dto.CategoryDto.CategoryResponse;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.service.ArticleService;
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

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ArticleControllerTest {

    private static final String API_BASE = "/api/articles";
    private static final String TEST_TITLE = "Test Article Title";
    private static final String TEST_CONTENT = "This is test article content";
    private static final String TEST_AUTHOR = "testAuthor";
    private static final String TEST_EDITOR = "testEditor";
    private static final Long TEST_ID = 1L;
    private static final Long CATEGORY_ID = 1L;
    private static final Instant TEST_PUBLISHED_AT = Instant.parse("2024-12-10T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArticleService articleService;

    @Test
    @WithMockUser(username = TEST_AUTHOR, roles = {"EDITOR"})
    void create_ShouldReturnArticleAnd201() throws Exception {
        CreateArticleRequest request = new CreateArticleRequest(
                TEST_TITLE,
                ArticleState.DRAFT,
                TEST_PUBLISHED_AT,
                TEST_CONTENT,
                Set.of(CATEGORY_ID),
                TEST_EDITOR
        );

        UserResponse authorResponse = new UserResponse(1L, TEST_AUTHOR, "Test Author", UserRole.EDITOR, true);
        UserResponse editorResponse = new UserResponse(2L, TEST_EDITOR, "Test Editor", UserRole.EDITOR, true);
        CategoryResponse categoryResponse = new CategoryResponse(CATEGORY_ID, "Technology","Technology description");

        ArticleDetailResponse mockResponse = ArticleDetailResponse.builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .articleState(ArticleState.DRAFT)
                .publishedAt(TEST_PUBLISHED_AT)
                .content(TEST_CONTENT)
                .currentVersion(1)
                .author(authorResponse)
                .editor(editorResponse)
                .categories(Set.of(categoryResponse))
                .build();

        when(articleService.create(any(CreateArticleRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post(API_BASE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT))
                .andExpect(jsonPath("$.articleState").value("DRAFT"))
                .andExpect(jsonPath("$.currentVersion").value(1))
                .andExpect(jsonPath("$.author.username").value(TEST_AUTHOR))
                .andExpect(jsonPath("$.editor.username").value(TEST_EDITOR));
    }

    @Test
    @WithMockUser(username = TEST_AUTHOR)
    void get_ShouldReturnArticleAnd200() throws Exception {
        UserResponse authorResponse = new UserResponse(1L, TEST_AUTHOR, "Test Author", UserRole.EDITOR, true);
        CategoryResponse categoryResponse = new CategoryResponse(CATEGORY_ID, "Technology","Technology description");

        ArticleDetailResponse mockResponse = ArticleDetailResponse.builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .articleState(ArticleState.PUBLISHED)
                .publishedAt(TEST_PUBLISHED_AT)
                .content(TEST_CONTENT)
                .currentVersion(1)
                .author(authorResponse)
                .editor(null)
                .categories(Set.of(categoryResponse))
                .build();

        when(articleService.get(TEST_ID)).thenReturn(mockResponse);

        mockMvc.perform(get(API_BASE + "/{id}", TEST_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT))
                .andExpect(jsonPath("$.articleState").value("PUBLISHED"))
                .andExpect(jsonPath("$.author.username").value(TEST_AUTHOR));
    }

    @Test
    @WithMockUser(username = TEST_AUTHOR)
    void list_ShouldReturnPaginatedArticlesAnd200() throws Exception {
        UserResponse author1 = new UserResponse(1L, "author1", "Author One", UserRole.EDITOR, true);
        UserResponse author2 = new UserResponse(2L, "author2", "Author Two", UserRole.EDITOR, true);
        CategoryResponse categoryResponse = new CategoryResponse(CATEGORY_ID, "Technology","Technology description");

        ArticleResponse article1 = ArticleResponse.builder()
                .id(1L)
                .title("Article 1")
                .articleState(ArticleState.PUBLISHED)
                .publishedAt(TEST_PUBLISHED_AT)
                .author(author1)
                .editor(null)
                .categories(Set.of(categoryResponse))
                .build();

        ArticleResponse article2 = ArticleResponse.builder()
                .id(2L)
                .title("Article 2")
                .articleState(ArticleState.DRAFT)
                .publishedAt(TEST_PUBLISHED_AT)
                .author(author2)
                .editor(null)
                .categories(Set.of(categoryResponse))
                .build();

        PaginatedArticleResponse mockResponse = new PaginatedArticleResponse(
                List.of(article1, article2),
                0,
                20,
                2L,
                1
        );

        when(articleService.list(any(Pageable.class), any())).thenReturn(mockResponse);

        mockMvc.perform(get(API_BASE)
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles").isArray())
                .andExpect(jsonPath("$.articles.length()").value(2))
                .andExpect(jsonPath("$.articles[0].title").value("Article 1"))
                .andExpect(jsonPath("$.articles[1].title").value("Article 2"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    @WithMockUser(username = TEST_AUTHOR, roles = {"EDITOR"})
    void update_ShouldReturnUpdatedArticleAnd200() throws Exception {
        UpdateArticleRequest request = new UpdateArticleRequest(
                "Updated Title",
                ArticleState.PUBLISHED,
                TEST_PUBLISHED_AT,
                "Updated Content",
                Set.of(CATEGORY_ID),
                TEST_EDITOR
        );

        UserResponse authorResponse = new UserResponse(1L, TEST_AUTHOR, "Test Author", UserRole.EDITOR, true);
        UserResponse editorResponse = new UserResponse(2L, TEST_EDITOR, "Test Editor", UserRole.EDITOR, true);
        CategoryResponse categoryResponse = new CategoryResponse(CATEGORY_ID, "Technology","Technology description");

        ArticleDetailResponse mockResponse = ArticleDetailResponse.builder()
                .id(TEST_ID)
                .title("Updated Title")
                .articleState(ArticleState.PUBLISHED)
                .publishedAt(TEST_PUBLISHED_AT)
                .content("Updated Content")
                .currentVersion(2)
                .author(authorResponse)
                .editor(editorResponse)
                .categories(Set.of(categoryResponse))
                .build();

        when(articleService.update(eq(TEST_ID), any(UpdateArticleRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put(API_BASE + "/{id}", TEST_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.articleState").value("PUBLISHED"))
                .andExpect(jsonPath("$.currentVersion").value(2));
    }

    @Test
    @WithMockUser(username = TEST_AUTHOR, roles = {"ADMIN"})
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(articleService).delete(TEST_ID);

        mockMvc.perform(delete(API_BASE + "/{id}", TEST_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
