package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.dto.ArticleDto.*;
import cz.upce.fei.redsys.repository.ArticleRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    private static final Long ARTICLE_ID = 1L;
    private static final String TEST_TITLE = "Test Article";
    private static final String TEST_CONTENT = "Test article content";
    private static final Instant TEST_PUBLISHED_AT = Instant.parse("2024-12-10T12:00:00Z");
    private static final Long CATEGORY_ID = 1L;

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserService userService;
    @Mock
    private AuthService authService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ArticleVersionService articleVersionService;

    @InjectMocks
    private ArticleService articleService;

    private User mockAuthor;
    private User mockEditor;
    private Article mockArticle;
    private ArticleVersion mockVersion;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockAuthor = User.builder()
                .id(1L)
                .username("author")
                .role(UserRole.EDITOR)
                .build();

        mockEditor = User.builder()
                .id(2L)
                .username("editor")
                .role(UserRole.EDITOR)
                .build();

        mockCategory = new Category();
        mockCategory.setId(CATEGORY_ID);
        mockCategory.setName("Technology");

        mockArticle = Article.builder()
                .id(ARTICLE_ID)
                .title(TEST_TITLE)
                .articleState(ArticleState.DRAFT)
                .author(mockAuthor)
                .editor(mockEditor)
                .publishedAt(TEST_PUBLISHED_AT)
                .categories(Set.of(mockCategory))
                .build();

        mockVersion = ArticleVersion.builder()
                .id(1L)
                .versionNumber(1)
                .content(TEST_CONTENT)
                .article(mockArticle)
                .createdBy(mockAuthor)
                .build();
    }

    @Test
    void create_ShouldSaveArticleAndReturnResponse() {
        CreateArticleRequest request = new CreateArticleRequest(
                TEST_TITLE,
                ArticleState.DRAFT,
                TEST_PUBLISHED_AT,
                TEST_CONTENT,
                Set.of(CATEGORY_ID),
                "editor"
        );

        when(authService.currentUser()).thenReturn(mockAuthor);
        when(userService.requireUserByIdentifier("editor")).thenReturn(mockEditor);
        when(categoryService.findAllByIds(Set.of(CATEGORY_ID))).thenReturn(List.of(mockCategory));
        when(articleRepository.save(any(Article.class))).thenReturn(mockArticle);
        when(articleVersionService.createInitialVersion(any(Article.class), eq(TEST_CONTENT)))
                .thenReturn(mockVersion);

        ArticleDetailResponse response = articleService.create(request);

        assertNotNull(response);
        assertEquals(ARTICLE_ID, response.id());
        assertEquals(TEST_TITLE, response.title());
        assertEquals(TEST_CONTENT, response.content());
        assertEquals(1, response.currentVersion());
        verify(articleRepository, times(1)).save(any(Article.class));
        verify(articleVersionService, times(1)).createInitialVersion(any(Article.class), eq(TEST_CONTENT));
    }

    @Test
    void create_ShouldCreateWithoutEditor_WhenEditorUsernameIsNull() {
        CreateArticleRequest request = new CreateArticleRequest(
                TEST_TITLE,
                ArticleState.DRAFT,
                TEST_PUBLISHED_AT,
                TEST_CONTENT,
                Set.of(CATEGORY_ID),
                null
        );

        when(authService.currentUser()).thenReturn(mockAuthor);
        when(categoryService.findAllByIds(Set.of(CATEGORY_ID))).thenReturn(List.of(mockCategory));
        when(articleRepository.save(any(Article.class))).thenReturn(mockArticle);
        when(articleVersionService.createInitialVersion(any(Article.class), eq(TEST_CONTENT)))
                .thenReturn(mockVersion);

        ArticleDetailResponse response = articleService.create(request);

        assertNotNull(response);
        verify(userService, never()).requireUserByIdentifier(any());
    }

    @Test
    void create_ShouldCreateWithEmptyCategories_WhenNoCategoriesProvided() {
        CreateArticleRequest request = new CreateArticleRequest(
                TEST_TITLE,
                ArticleState.DRAFT,
                TEST_PUBLISHED_AT,
                TEST_CONTENT,
                Set.of(),
                null
        );

        when(authService.currentUser()).thenReturn(mockAuthor);
        when(articleRepository.save(any(Article.class))).thenReturn(mockArticle);
        when(articleVersionService.createInitialVersion(any(Article.class), eq(TEST_CONTENT)))
                .thenReturn(mockVersion);

        ArticleDetailResponse response = articleService.create(request);

        assertNotNull(response);
        verify(categoryService, never()).findAllByIds(any());
    }

    @Test
    void get_ShouldReturnArticleWithLatestVersion() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(mockArticle));
        when(articleVersionService.getLatestVersion(mockArticle)).thenReturn(mockVersion);

        ArticleDetailResponse response = articleService.get(ARTICLE_ID);

        assertNotNull(response);
        assertEquals(ARTICLE_ID, response.id());
        assertEquals(TEST_TITLE, response.title());
        assertEquals(TEST_CONTENT, response.content());
        verify(articleRepository, times(1)).findById(ARTICLE_ID);
        verify(articleVersionService, times(1)).getLatestVersion(mockArticle);
    }

    @Test
    void get_ShouldThrowEntityNotFoundException_WhenArticleNotFound() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> articleService.get(ARTICLE_ID));
        verify(articleVersionService, never()).getLatestVersion(any());
    }

    @Test
    void list_ShouldReturnPaginatedArticles() {
        Pageable pageable = PageRequest.of(0, 20);
        Article article2 = Article.builder()
                .id(2L)
                .title("Article 2")
                .articleState(ArticleState.PUBLISHED)
                .author(mockAuthor)
                .publishedAt(TEST_PUBLISHED_AT)
                .categories(Set.of())
                .build();

        Page<Article> page = new PageImpl<>(List.of(mockArticle, article2), pageable, 2);
        when(articleRepository.findAll(pageable)).thenReturn(page);

        PaginatedArticleResponse response = articleService.list(pageable, new ArrayList<>());

        assertNotNull(response);
        assertEquals(2, response.articles().size());
        assertEquals(0, response.page());
        assertEquals(20, response.size());
        assertEquals(2, response.totalElements());
        assertEquals(1, response.totalPages());
        verify(articleRepository, times(1)).findAll(pageable);
    }

    @Test
    void list_ShouldReturnEmptyList_WhenNoArticles() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Article> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(articleRepository.findAll(pageable)).thenReturn(emptyPage);

        PaginatedArticleResponse response = articleService.list(pageable, new ArrayList<>());

        assertNotNull(response);
        assertEquals(0, response.articles().size());
        assertEquals(0, response.totalElements());
    }

    @Test
    void update_ShouldUpdateArticleAndCreateNewVersion() {
        UpdateArticleRequest request = new UpdateArticleRequest(
                "Updated Title",
                ArticleState.PUBLISHED,
                TEST_PUBLISHED_AT,
                "Updated content",
                Set.of(CATEGORY_ID),
                "editor"
        );

        Article updatedArticle = Article.builder()
                .id(ARTICLE_ID)
                .title("Updated Title")
                .articleState(ArticleState.PUBLISHED)
                .author(mockAuthor)
                .editor(mockEditor)
                .publishedAt(TEST_PUBLISHED_AT)
                .categories(Set.of(mockCategory))
                .build();

        ArticleVersion newVersion = ArticleVersion.builder()
                .id(2L)
                .versionNumber(2)
                .content("Updated content")
                .article(updatedArticle)
                .createdBy(mockEditor)
                .build();

        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(mockArticle));
        when(authService.currentUser()).thenReturn(mockEditor);
        when(userService.requireUserByIdentifier("editor")).thenReturn(mockEditor);
        when(categoryService.findAllByIds(Set.of(CATEGORY_ID))).thenReturn(List.of(mockCategory));
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);
        when(articleVersionService.createNewVersionIfChanged(any(Article.class), eq("Updated content")))
                .thenReturn(newVersion);

        ArticleDetailResponse response = articleService.update(ARTICLE_ID, request);

        assertNotNull(response);
        assertEquals("Updated Title", response.title());
        assertEquals("Updated content", response.content());
        assertEquals(2, response.currentVersion());
        verify(articleRepository, times(1)).save(any(Article.class));
        verify(articleVersionService, times(1)).createNewVersionIfChanged(any(Article.class), eq("Updated content"));
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenArticleNotFound() {
        UpdateArticleRequest request = new UpdateArticleRequest(
                "Updated Title",
                ArticleState.PUBLISHED,
                TEST_PUBLISHED_AT,
                "Updated content",
                Set.of(CATEGORY_ID),
                null
        );

        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> articleService.update(ARTICLE_ID, request));
        verify(articleRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowAccessDeniedException_WhenEditorNotAssigned() {
        UpdateArticleRequest request = new UpdateArticleRequest(
                "Updated Title",
                ArticleState.PUBLISHED,
                TEST_PUBLISHED_AT,
                "Updated content",
                Set.of(CATEGORY_ID),
                null
        );

        User unauthorizedEditor = User.builder()
                .id(3L)
                .username("unauthorized")
                .role(UserRole.EDITOR)
                .build();

        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(mockArticle));
        when(authService.currentUser()).thenReturn(unauthorizedEditor);

        assertThrows(AccessDeniedException.class, () -> articleService.update(ARTICLE_ID, request));
        verify(articleRepository, never()).save(any());
    }

    @Test
    void update_ShouldAllowAdmin_EvenIfNotAssigned() {
        UpdateArticleRequest request = new UpdateArticleRequest(
                "Updated Title",
                ArticleState.PUBLISHED,
                TEST_PUBLISHED_AT,
                "Updated content",
                Set.of(CATEGORY_ID),
                null
        );

        User adminUser = User.builder()
                .id(3L)
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        Article updatedArticle = Article.builder()
                .id(ARTICLE_ID)
                .title("Updated Title")
                .articleState(ArticleState.PUBLISHED)
                .author(mockAuthor)
                .editor(mockEditor)
                .publishedAt(TEST_PUBLISHED_AT)
                .categories(Set.of(mockCategory))
                .build();

        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(mockArticle));
        when(authService.currentUser()).thenReturn(adminUser);
        when(categoryService.findAllByIds(Set.of(CATEGORY_ID))).thenReturn(List.of(mockCategory));
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);
        when(articleVersionService.createNewVersionIfChanged(any(Article.class), eq("Updated content")))
                .thenReturn(mockVersion);

        ArticleDetailResponse response = articleService.update(ARTICLE_ID, request);

        assertNotNull(response);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void delete_ShouldDeleteArticle_WhenExists() {
        when(articleRepository.existsById(ARTICLE_ID)).thenReturn(true);

        assertDoesNotThrow(() -> articleService.delete(ARTICLE_ID));

        verify(articleRepository, times(1)).existsById(ARTICLE_ID);
        verify(articleRepository, times(1)).deleteById(ARTICLE_ID);
    }

    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenArticleNotFound() {
        when(articleRepository.existsById(ARTICLE_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> articleService.delete(ARTICLE_ID));
        verify(articleRepository, never()).deleteById(any());
    }

    @Test
    void requireArticleById_ShouldReturnArticle_WhenExists() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(mockArticle));

        Article result = articleService.requireArticleById(ARTICLE_ID);

        assertNotNull(result);
        assertEquals(ARTICLE_ID, result.getId());
        assertEquals(TEST_TITLE, result.getTitle());
    }

    @Test
    void requireArticleById_ShouldThrowEntityNotFoundException_WhenNotFound() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> articleService.requireArticleById(ARTICLE_ID));
    }
}