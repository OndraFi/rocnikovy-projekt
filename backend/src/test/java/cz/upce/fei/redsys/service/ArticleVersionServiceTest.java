package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleVersion;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.dto.ArticleVersionDto.PaginatedArticleVersionResponse;
import cz.upce.fei.redsys.repository.ArticleVersionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleVersionServiceTest {

    private static final Long ARTICLE_ID = 1L;

    @Mock
    private ArticleVersionRepository versionRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ArticleVersionService articleVersionService;

    private Article article;
    private User author;
    private User editor;
    private ArticleVersion version1;
    private ArticleVersion version2;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(1L)
                .username("author")
                .role(UserRole.EDITOR)
                .build();

        editor = User.builder()
                .id(2L)
                .username("editor")
                .role(UserRole.EDITOR)
                .build();

        article = Article.builder()
                .id(ARTICLE_ID)
                .title("Test article")
                .author(author)
                .editor(editor)
                .build();

        version1 = ArticleVersion.builder()
                .id(1L)
                .article(article)
                .versionNumber(1)
                .content("Initial content")
                .createdBy(author)
                .build();

        version2 = ArticleVersion.builder()
                .id(2L)
                .article(article)
                .versionNumber(2)
                .content("Updated content")
                .createdBy(editor)
                .build();
    }

    // createInitialVersion

    @Test
    void createInitialVersion_ShouldCreateVersionWithNumber1AndCurrentUser() {
        when(authService.currentUser()).thenReturn(author);
        when(versionRepository.save(any(ArticleVersion.class))).thenAnswer(invocation -> {
            ArticleVersion v = invocation.getArgument(0);
            v.setId(1L);
            return v;
        });

        ArticleVersion result = articleVersionService.createInitialVersion(article, "Initial content");

        assertNotNull(result);
        assertEquals(article, result.getArticle());
        assertEquals("Initial content", result.getContent());
        assertEquals(1, result.getVersionNumber());
        assertEquals(author, result.getCreatedBy());
        verify(versionRepository, times(1)).save(any(ArticleVersion.class));
    }

    // createNewVersionIfChanged

    @Test
    void createNewVersionIfChanged_ShouldCreateNewVersion_WhenContentDiffers() {
        when(authService.currentUser()).thenReturn(editor);
        when(versionRepository.findTopByArticleOrderByVersionNumberDesc(article))
                .thenReturn(Optional.of(version1));
        when(versionRepository.save(any(ArticleVersion.class))).thenAnswer(invocation -> {
            ArticleVersion v = invocation.getArgument(0);
            v.setId(2L);
            return v;
        });

        ArticleVersion result = articleVersionService.createNewVersionIfChanged(article, "Updated content");

        assertNotNull(result);
        assertEquals(2, result.getVersionNumber());
        assertEquals("Updated content", result.getContent());
        assertEquals(editor, result.getCreatedBy());
        verify(versionRepository, times(1)).save(any(ArticleVersion.class));
    }

    @Test
    void createNewVersionIfChanged_ShouldReturnCurrentVersion_WhenContentUnchanged() {
        when(authService.currentUser()).thenReturn(editor);
        when(versionRepository.findTopByArticleOrderByVersionNumberDesc(article))
                .thenReturn(Optional.of(version1));

        ArticleVersion result = articleVersionService.createNewVersionIfChanged(article, "Initial content");

        assertSame(version1, result);
        verify(versionRepository, never()).save(any());
    }

    // getLatestVersion

    @Test
    void getLatestVersion_ShouldReturnLatestVersion_WhenExists() {
        when(versionRepository.findTopByArticleOrderByVersionNumberDesc(article))
                .thenReturn(Optional.of(version2));

        ArticleVersion result = articleVersionService.getLatestVersion(article);

        assertSame(version2, result);
        verify(versionRepository, times(1))
                .findTopByArticleOrderByVersionNumberDesc(article);
    }

    @Test
    void getLatestVersion_ShouldThrowIllegalStateException_WhenNoVersions() {
        when(versionRepository.findTopByArticleOrderByVersionNumberDesc(article))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> articleVersionService.getLatestVersion(article));
    }

    // getVersion – permissions + not found

    @Test
    void getVersion_ShouldReturnVersion_WhenAdminAndVersionExists() {
        User admin = User.builder()
                .id(10L)
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        when(authService.currentUser()).thenReturn(admin);
        when(versionRepository.findByArticleAndVersionNumber(article, 1))
                .thenReturn(Optional.of(version1));

        ArticleVersion result = articleVersionService.getVersion(article, 1);

        assertSame(version1, result);
    }

    @Test
    void getVersion_ShouldReturnVersion_WhenEditorAssignedToArticle() {
        when(authService.currentUser()).thenReturn(editor);
        when(versionRepository.findByArticleAndVersionNumber(article, 1))
                .thenReturn(Optional.of(version1));

        ArticleVersion result = articleVersionService.getVersion(article, 1);

        assertSame(version1, result);
    }

    @Test
    void getVersion_ShouldThrowAccessDenied_WhenEditorNotAssigned() {
        User otherEditor = User.builder()
                .id(3L)
                .username("other")
                .role(UserRole.EDITOR)
                .build();

        when(authService.currentUser()).thenReturn(otherEditor);

        assertThrows(AccessDeniedException.class,
                () -> articleVersionService.getVersion(article, 1));

        verify(versionRepository, never())
                .findByArticleAndVersionNumber(any(), any());
    }

    @Test
    void getVersion_ShouldThrowAccessDenied_ForUnsupportedRole() {
        User reader = User.builder()
                .id(4L)
                .username("reader")
                .role(UserRole.USER)
                .build();

        when(authService.currentUser()).thenReturn(reader);

        assertThrows(AccessDeniedException.class,
                () -> articleVersionService.getVersion(article, 1));

        verify(versionRepository, never())
                .findByArticleAndVersionNumber(any(), any());
    }

    @Test
    void getVersion_ShouldThrowEntityNotFound_WhenVersionNotFound() {
        when(authService.currentUser()).thenReturn(editor);
        when(versionRepository.findByArticleAndVersionNumber(article, 99))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> articleVersionService.getVersion(article, 99));
    }

    // listVersions – pagination + permissions

    @Test
    void listVersions_ShouldReturnPaginatedVersions_WhenUserHasPermission() {
        Pageable pageable = Pageable.ofSize(10);
        Page<ArticleVersion> page = new PageImpl<>(
                List.of(version2, version1), pageable, 2);

        when(authService.currentUser()).thenReturn(editor);
        when(versionRepository.findAllByArticleOrderByVersionNumberDesc(article, pageable))
                .thenReturn(page);

        PaginatedArticleVersionResponse response =
                articleVersionService.listVersions(article, pageable);

        assertNotNull(response);
        assertEquals(2, response.versions().size());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals(2, response.totalElements());
        assertEquals(1, response.totalPages());
        verify(versionRepository, times(1))
                .findAllByArticleOrderByVersionNumberDesc(article, pageable);
    }

    @Test
    void listVersions_ShouldThrowAccessDenied_WhenUserHasNoPermission() {
        User reader = User.builder()
                .id(4L)
                .username("reader")
                .role(UserRole.USER)
                .build();

        Pageable pageable = Pageable.ofSize(10);

        when(authService.currentUser()).thenReturn(reader);

        assertThrows(AccessDeniedException.class,
                () -> articleVersionService.listVersions(article, pageable));

        verify(versionRepository, never())
                .findAllByArticleOrderByVersionNumberDesc(any(), any());
    }
}
