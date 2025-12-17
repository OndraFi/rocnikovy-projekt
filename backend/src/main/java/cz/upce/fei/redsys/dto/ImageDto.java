package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.Image;
import lombok.Builder;
import org.springframework.core.io.Resource;

import java.time.Instant;
import java.util.List;

public final class ImageDto {
    private ImageDto() {}

    @Builder
    public record ImageResponse(
            Long id,
            String filename,
            String originalFilename,
            String contentType,
            Long fileSize,
            Instant uploadedAt,
            String url
    ) {}

    public record PaginatedImageResponse(
            List<ImageResponse> categories,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record DownloadResource(
            Resource resource,
            String contentType,
            long contentLength,
            String originalFilename
    ) {}

    public static ImageResponse toResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .filename(image.getFilename())
                .originalFilename(image.getOriginalFilename())
                .contentType(image.getContentType())
                .fileSize(image.getFileSize())
                .uploadedAt(image.getUploadedAt())
                .url("/api/images/" + image.getId())
                .build();
    }
}