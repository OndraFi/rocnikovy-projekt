package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleState;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.ArticleDto;
import cz.upce.fei.redsys.dto.ArticleDto.*;
import cz.upce.fei.redsys.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final AuthService authService;

    @Transactional
    public ArticleResponse create(CreateArticleRequest req) {
        User editor = userService.requireUserByIdentifier(req.editorUsername());

        Article article = Article.builder()
                .title(req.title())
                .articleState(req.articleState() != null ? req.articleState() : ArticleState.DRAFT)
                .author(authService.currentUser())
                .editor(editor)
                .publishedAt(req.publishedAt())
                .build();

        return ArticleDto.toResponse(articleRepository.save(article));
    }

    @Transactional(readOnly = true)
    public ArticleResponse get(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
        return ArticleDto.toResponse(article);
    }

    public PaginatedArticleResponse list(Pageable pageable) {
        Page<Article> page = articleRepository.findAll(pageable);
        List<ArticleResponse> articles = page.getContent().stream()
                .map(ArticleDto::toResponse)
                .toList();

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
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
        User editor = userService.requireUserByIdentifier(req.editorUsername());

        if (req.title() != null) article.setTitle(req.title());
        if (req.articleState() != null) article.setArticleState(req.articleState());
        if (req.publishedAt() != null) article.setPublishedAt(req.publishedAt());

        article.setEditor(editor);

        return ArticleDto.toResponse(articleRepository.save(article));
    }

    @Transactional
    public void delete(Long id) {
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
