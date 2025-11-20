package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleVersion;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.ArticleVersionDto;
import cz.upce.fei.redsys.dto.ArticleVersionDto.ArticleVersionResponse;
import cz.upce.fei.redsys.dto.ArticleVersionDto.PaginatedArticleVersionResponse;
import cz.upce.fei.redsys.repository.ArticleVersionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleVersionService {

    private final ArticleVersionRepository versionRepository;
    private final AuthService authService;

    @Transactional
    public ArticleVersion createInitialVersion(Article article, String content) {
        log.debug("Creating initial version for article {}", article.getId());
        User currentUser = authService.currentUser();

        ArticleVersion version = ArticleVersion.builder()
                .article(article)
                .content(content)
                .versionNumber(1)
                .createdBy(currentUser)
                .build();

        return versionRepository.save(version);
    }

    @Transactional
    public ArticleVersion createNewVersionIfChanged(Article article, String newContent) {
        log.debug("Checking if new version is needed for article {}", article.getId());
        User currentUser = authService.currentUser();

        ArticleVersion currentVersion = getLatestVersion(article);

        if (!newContent.equals(currentVersion.getContent())) {
            log.debug("Content changed, creating new version");
            ArticleVersion version = ArticleVersion.builder()
                    .article(article)
                    .content(newContent)
                    .versionNumber(currentVersion.getVersionNumber() + 1)
                    .createdBy(currentUser)
                    .build();

            return versionRepository.save(version);
        }

        log.debug("Content unchanged, keeping version {}", currentVersion.getVersionNumber());
        return currentVersion;
    }

    @Transactional(readOnly = true)
    public ArticleVersion getLatestVersion(Article article) {
        log.debug("Getting latest version for article {}", article.getId());
        return versionRepository.findTopByArticleOrderByVersionNumberDesc(article)
                .orElseThrow(() -> new IllegalStateException("Article has no versions"));
    }

    @Transactional(readOnly = true)
    public ArticleVersion getVersion(Article article, Integer versionNumber) {
        enforceViewPermission(article);
        log.debug("Getting version {} for article {}", versionNumber, article.getId());
        return versionRepository.findByArticleAndVersionNumber(article, versionNumber)
                .orElseThrow(() -> new EntityNotFoundException("Version " + versionNumber + " not found for article " + article.getId()));
    }

    @Transactional(readOnly = true)
    public PaginatedArticleVersionResponse listVersions(Article article, Pageable pageable) {
        enforceViewPermission(article);
        log.debug("Listing versions for article {}: {}", article.getId(), pageable);
        Page<ArticleVersion> page = versionRepository.findAllByArticleOrderByVersionNumberDesc(article, pageable);

        List<ArticleVersionResponse> articleVersions = page.getContent().stream()
                .map(version -> ArticleVersionDto.toResponse(version, false))
                .toList();

        log.debug("Found {} versions", articleVersions.size());

        return new PaginatedArticleVersionResponse(
                articleVersions,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private void enforceViewPermission(Article article) {
        User currentUser = authService.currentUser();
        switch (currentUser.getRole()) {
            case ADMIN, CHIEF_EDITOR, REVIEWER -> {}
            case EDITOR -> {
                if (article.getEditor() == null || !article.getEditor().getId().equals(currentUser.getId())) {
                    throw new AccessDeniedException("You cannot access the versions of this article");
                }
            }
            default -> throw new AccessDeniedException("You cannot access the versions of this article");
        }
    }
}