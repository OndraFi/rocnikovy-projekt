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
            LocalDateTime createDate,
            LocalDateTime updateDate,
            TicketState state,
            String asignedUserName,
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

            @NotNull(message = "{common.required}")
            TicketType type,

            @NotNull(message = "{common.required}")
            TicketPriority priority
    ) {}

    public record UpdateTicketRequest(
            @NotBlank(message = "{common.required}")
            @Size(min = 1, max = 160, message = "{ticket.title.size}")
            @Pattern(regexp = "^[\\p{L}\\p{N}\\s.,!?;:'\"()\\[\\]{}\\-_/]+$",message = "{ticket.title.pattern}")
            String title,

            @NotNull(message = "{common.required}")
            TicketType type,

            @NotNull(message = "{common.required}")
            TicketPriority priority,

            @NotNull(message = "{common.required}")
            TicketState state
    ) {}

    public static TicketResponse toTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .number(ticket.getProjectTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createDate(ticket.getCreateDate())
                .updateDate(ticket.getUpdateDate())
                .state(ticket.getState())
                .asignedUserName(ticket.getAsignedUser() != null ? ticket.getAsignedUser().getFullName() : null)
                .authorName(ticket.getAuthor() != null ? ticket.getAuthor().getFullName() : null)
                .articleId(ticket.getArticle() != null ? ticket.getArticle().getId() : null)
                .build();
    }

}