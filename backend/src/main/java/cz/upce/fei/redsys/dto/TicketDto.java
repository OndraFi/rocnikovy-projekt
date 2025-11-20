package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

public final class TicketDto {
    private TicketDto() {}

    @Builder
    public record TicketResponse(
            Long id,
            String title,
            String description,
            Instant createdAt,
            Instant updatedAt,
            TicketState state,
            UserResponse assignee,
            UserResponse author,
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
            String assigneeUsername,

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

            String assigneeUsername
    ) {}

    public record TransitionTicketRequest(
            @NotNull(message = "{common.required}")
            TicketState targetState,

            @Size(max = 2000, message = "{ticket.comment.size}")
            String comment
    ) {}

    public static TicketResponse toTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .state(ticket.getState())
                .assignee(ticket.getAssignee() != null ? UserDto.toUserResponse(ticket.getAssignee()) : null)
                .author(ticket.getAuthor() != null ? UserDto.toUserResponse(ticket.getAuthor()) : null)
                .articleId(ticket.getArticle() != null ? ticket.getArticle().getId() : null)
                .build();
    }
}