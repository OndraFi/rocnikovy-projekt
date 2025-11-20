package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleState;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.dto.CategoryDto.CategoryResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ArticleDto {
    private ArticleDto() {}

    public record CreateArticleRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 5, max = 255, message = "{article.title.size}")
            String title,

            @NotNull(message = "{common.required}")
            ArticleState articleState,

            @NotNull(message = "{common.required}")
            Instant publishedAt,

            @NotBlank(message = "{common.required}")
            String content,

            @NotNull(message = "{common.required}")
            Set<Long> categoryIds,

            String editorUsername
    ) {}

    public record UpdateArticleRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 5, max = 255, message = "{article.title.size}")
            String title,

            @NotNull(message = "{common.required}")
            ArticleState articleState,

            @NotNull(message = "{common.required}")
            Instant publishedAt,

            @NotNull(message = "{common.required}")
            String content,

            @NotNull(message = "{common.required}")
            Set<Long> categoryIds,

            String editorUsername
    ) {}

    @Builder
    public record ArticleResponse(
            Long id,
            String title,
            ArticleState articleState,
            Instant publishedAt,
            UserResponse author,
            UserResponse editor,
            Set<CategoryResponse> categories
    ) {}

    @Builder
    public record ArticleDetailResponse(
            Long id,
            String title,
            ArticleState articleState,
            Instant publishedAt,
            String content,
            Integer currentVersion,
            UserResponse author,
            UserResponse editor,
            Set<CategoryResponse> categories
    ) {}

    public record PaginatedArticleResponse(
            List<ArticleResponse> articles,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public static ArticleResponse toResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .articleState(article.getArticleState())
                .publishedAt(article.getPublishedAt())
                .author(article.getAuthor() != null ? UserDto.toUserResponse(article.getAuthor()) : null)
                .editor(article.getEditor() != null ? UserDto.toUserResponse(article.getEditor()) : null)
                .categories(article.getCategories().stream()
                        .map(CategoryDto::toResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static ArticleDetailResponse toDetailResponse(Article article, String content, Integer versionNumber) {
        return ArticleDetailResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .articleState(article.getArticleState())
                .publishedAt(article.getPublishedAt())
                .content(content)
                .currentVersion(versionNumber)
                .author(article.getAuthor() != null ? UserDto.toUserResponse(article.getAuthor()) : null)
                .editor(article.getEditor() != null ? UserDto.toUserResponse(article.getEditor()) : null)
                .categories(article.getCategories().stream()
                        .map(CategoryDto::toResponse)
                        .collect(Collectors.toSet()))
                .build();
    }
}
