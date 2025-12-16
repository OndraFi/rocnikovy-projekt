package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.dto.ArticleDto;
import cz.upce.fei.redsys.dto.ArticleDto.*;
import cz.upce.fei.redsys.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final AuthService authService;
    private final CategoryService categoryService;
    private final ArticleVersionService articleVersionService;

    @Transactional
    public ArticleDetailResponse create(CreateArticleRequest req) {
        log.debug("Creating article with title '{}'", req.title());
        User currentUser = authService.currentUser();
        User editor = req.editorUsername() != null && !req.editorUsername().isBlank()
                ? userService.requireUserByIdentifier(req.editorUsername())
                : null;

        Set<Category> categories = new HashSet<>();
        if (!req.categoryIds().isEmpty()) {
            categories.addAll(categoryService.findAllByIds(req.categoryIds()));
        }

        Article article = Article.builder()
                .title(req.title())
                .articleState(req.articleState())
                .author(currentUser)
                .editor(editor)
                .publishedAt(req.publishedAt())
                .categories(categories)
                .build();

        article = articleRepository.save(article);
        log.debug("Article saved: {}", ArticleDto.toResponse(article));

        ArticleVersion version = articleVersionService.createInitialVersion(article, req.content());
        return ArticleDto.toDetailResponse(article, version.getContent(), version.getVersionNumber());
    }

    @Transactional(readOnly = true)
    public ArticleDetailResponse get(Long id) {
        log.debug("Getting article with id {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
        log.debug("Article found: {}", ArticleDto.toResponse(article));

        ArticleVersion latestVersion = articleVersionService.getLatestVersion(article);
        return ArticleDto.toDetailResponse(article, latestVersion.getContent(), latestVersion.getVersionNumber());
    }

    public PaginatedArticleResponse list(Pageable pageable, Long categoryId) {
        log.debug("Listing articles: pageable={}, categoryId={}", pageable, categoryId);

        Page<Article> page;
        if (categoryId != null) {
            log.debug("Listing with filter");
            page = articleRepository.findByCategories_Id(categoryId, pageable);
        } else {
            log.debug("Listing without filter");
            page = articleRepository.findAll(pageable);
        }

        List<ArticleResponse> articles = page.getContent().stream()
                .map(ArticleDto::toResponse)
                .toList();

        log.debug("Found {} articles", articles.size());

        return new PaginatedArticleResponse(
                articles,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public ArticleDetailResponse update(Long id, UpdateArticleRequest req) {
        log.debug("Updating article with id {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
        User currentUser = authService.currentUser();
        if (currentUser.getRole() == UserRole.EDITOR &&
                (article.getEditor() == null || !currentUser.getId().equals(article.getEditor().getId()))) {
            throw new AccessDeniedException("You are not assigned to edit this article");
        }

        User editor = req.editorUsername() != null && !req.editorUsername().isBlank()
                ? userService.requireUserByIdentifier(req.editorUsername())
                : null;

        article.setTitle(req.title());
        article.setArticleState(req.articleState());
        article.setPublishedAt(req.publishedAt());
        Set<Category> categories = new HashSet<>();
        if (!req.categoryIds().isEmpty()) {
            categories.addAll(categoryService.findAllByIds(req.categoryIds()));
        }
        article.setCategories(categories);
        article.setEditor(editor);

        article = articleRepository.save(article);
        log.debug("Updated article: {}", ArticleDto.toResponse(article));

        ArticleVersion latestVersion = articleVersionService.createNewVersionIfChanged(article, req.content());
        return ArticleDto.toDetailResponse(article, latestVersion.getContent(), latestVersion.getVersionNumber());
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting article with id {}", id);
        if (!articleRepository.existsById(id)) {
            throw new EntityNotFoundException("Article not found");
        }
        articleRepository.deleteById(id);
    }

    public Article requireArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
    }
}
