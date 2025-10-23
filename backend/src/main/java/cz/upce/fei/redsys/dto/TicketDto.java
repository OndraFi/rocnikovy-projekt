package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public final class TicketDto {
    private TicketDto() {}

    @Builder
    public record TicketResponse(
            Long number,
            String title,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            TicketState state,
            String assignedUsername,
            String authorName,
            Long articleId
    ) {}

    public record PaginatedTicketResponse(
            List<TicketResponse> tickets,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record CreateTicketRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 1, max = 160, message = "{ticket.title.size}")
            @Pattern(regexp = "^[\\p{L}\\p{N}\\s.,!?;:'\"()\\[\\]{}\\-_/]+$",message = "{ticket.title.pattern}")
            String title,

            @Size(max = 2000, message = "{ticket.description.size}")
            String description,

            @NotNull(message = "{common.required}")
            Long assignedUserId,

            @NotNull(message = "{common.required}")
            Long authorId,

            @NotNull(message = "{common.required}")
            Long articleId
    ) {}

    public record UpdateTicketRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 1, max = 160, message = "{ticket.title.size}")
            @Pattern(regexp = "^[\\p{L}\\p{N}\\s.,!?;:'\"()\\[\\]{}\\-_/]+$",message = "{ticket.title.pattern}")
            String title,

            @Size(max = 2000, message = "{ticket.description.size}")
            String description,

            @NotNull(message = "{common.required}")
            TicketState state,

            @NotNull(message = "{common.required}")
            Long assignedUserId
    ) {}

    public static TicketResponse toTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .number(ticket.getProjectTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .state(ticket.getState())
                .assignedUsername(ticket.getAssignedUser() != null ? ticket.getAssignedUser().getFullName() : null)
                .authorName(ticket.getAuthor() != null ? ticket.getAuthor().getFullName() : null)
                .articleId(ticket.getArticle() != null ? ticket.getArticle().getId() : null)
                .build();
    }

}