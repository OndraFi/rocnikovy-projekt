package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.Image;
import lombok.Builder;

import java.time.Instant;

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