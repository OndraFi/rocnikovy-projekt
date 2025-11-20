package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

public final class CategoryDto {
    private CategoryDto() {}

    public record CreateCategoryRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 3, max = 50, message = "{category.name.size}")
            String name,

            @Size(max = 1000, message = "{category.description.size}")
            String description
    ) {}

    public record UpdateCategoryRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 3, max = 50, message = "{category.name.size}")
            String name,

            @Size(max = 1000, message = "{category.description.size}")
            String description
    ) {}

    @Builder
    public record CategoryResponse(
            Long id,
            String name,
            String description
    ) {}

    public record PaginatedCategoryResponse(
            List<CategoryResponse> categories,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}