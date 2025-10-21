package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.Project;
import cz.upce.fei.redsys.domain.ProjectState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

public final class ProjectDto {
    private ProjectDto() {}

    @Builder
    public record ProjectResponse(
            Long id,
            String name,
            String description,
            ProjectState state,
            Long ownerId
    ) {}

    public record PaginatedProjectResponse(
            List<ProjectResponse> projects,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record CreateProjectRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 5, max = 120, message = "{project.name.size}")
            @Pattern(regexp = "^[\\p{L}\\p{N}\\s._\\-():/]+$", message = "{project.name.pattern}")
            String name,

            @Size(max = 2000, message = "{project.description.size}")
            String description
    ) {}

    public record UpdateProjectRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 5, max = 120, message = "{project.name.size}")
            @Pattern(regexp = "^[\\p{L}\\p{N}\\s._\\-():/]+$", message = "{project.name.pattern}")
            String name,

            @Size(max = 2000, message = "{project.description.size}")
            String description,

            @NotNull(message = "{common.required}")
            ProjectState state
    ) {}

    public static ProjectResponse toProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .state(project.getState())
                .ownerId(project.getOwner() != null ? project.getOwner().getId() : null)
                .build();
    }
}