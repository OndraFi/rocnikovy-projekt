package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleState;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class ArticleDto {

    public record CreateArticleRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 5, max = 255, message = "{article.title.size}")
            String title,

            @NotNull(message = "{common.required}")
            ArticleState articleState,

            @NotNull(message = "{common.required}")
            Instant publishedAt,

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

            String editorUsername
    ) {}

    public record ArticleResponse(
            Long id,
            String title,
            ArticleState articleState,
            Instant publishedAt,
            UserResponse author,
            UserResponse editor
    ) {}

    public record PaginatedArticleResponse(
            List<ArticleResponse> articles,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public static ArticleResponse toResponse(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getArticleState(),
                article.getPublishedAt(),
                article.getAuthor() != null ? UserDto.toUserResponse(article.getAuthor()) : null,
                article.getEditor() != null ? UserDto.toUserResponse(article.getEditor()) : null
        );
    }
}
