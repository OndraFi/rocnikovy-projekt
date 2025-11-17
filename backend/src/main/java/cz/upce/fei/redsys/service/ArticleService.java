package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleState;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.ArticleDto;
import cz.upce.fei.redsys.dto.ArticleDto.*;
import cz.upce.fei.redsys.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final AuthService authService;

    @Transactional
    public ArticleResponse create(CreateArticleRequest req) {
        log.debug("Creating article with title '{}'", req.title());
        User editor = req.editorUsername() != null && !req.editorUsername().isBlank()
                ? userService.requireUserByIdentifier(req.editorUsername())
                : null;

        Article article = Article.builder()
                .title(req.title())
                .articleState(req.articleState() != null ? req.articleState() : ArticleState.DRAFT)
                .author(authService.currentUser())
                .editor(editor)
                .publishedAt(req.publishedAt())
                .build();

        ArticleResponse response = ArticleDto.toResponse(articleRepository.save(article));
        log.debug("Article saved: {}", response);
        return response;
    }

    @Transactional(readOnly = true)
    public ArticleResponse get(Long id) {
        log.debug("Getting article with id {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
        ArticleResponse response = ArticleDto.toResponse(article);
        log.debug("Article found: {}", response);
        return response;
    }

    public PaginatedArticleResponse list(Pageable pageable) {
        log.debug("Listing articles: {}", pageable);
        Page<Article> page = articleRepository.findAll(pageable);
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
    public ArticleResponse update(Long id, UpdateArticleRequest req) {
        log.debug("Updating article with id {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
        User editor = req.editorUsername() != null && !req.editorUsername().isBlank()
                ? userService.requireUserByIdentifier(req.editorUsername())
                : null;

        if (req.title() != null && !req.title().isBlank()) article.setTitle(req.title());
        if (req.articleState() != null) article.setArticleState(req.articleState());
        if (req.publishedAt() != null) article.setPublishedAt(req.publishedAt());

        article.setEditor(editor);

        ArticleResponse response = ArticleDto.toResponse(articleRepository.save(article));
        log.debug("Updated article: {}", response);
        return response;
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
