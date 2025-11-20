package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.ArticleVersion;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

public final class ArticleVersionDto {
    private ArticleVersionDto() {}

    @Builder
    public record ArticleVersionResponse(
            Long id,
            Integer versionNumber,
            Instant createdAt,
            UserResponse createdBy,
            String content
    ) {}

    public record PaginatedArticleVersionResponse(
            List<ArticleVersionResponse> versions,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public static ArticleVersionResponse toResponse(ArticleVersion version, boolean includeContent) {
        return ArticleVersionResponse.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .createdAt(version.getCreatedAt())
                .createdBy(version.getCreatedBy() != null ? UserDto.toUserResponse(version.getCreatedBy()) : null)
                .content(includeContent ? version.getContent() : null)
                .build();
    }
}