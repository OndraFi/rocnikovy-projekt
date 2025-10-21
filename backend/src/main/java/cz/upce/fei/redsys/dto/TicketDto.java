package cz.upce.fei.redsys.dto;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketPriority;
import cz.upce.fei.redsys.domain.TicketState;
import cz.upce.fei.redsys.domain.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

public final class TicketDto {
    private TicketDto() {}

    @Builder
    public record TicketResponse(
            Long number,
            String title,
            TicketType type,
            TicketPriority priority,
            TicketState state,
            Long projectId
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
                .type(ticket.getType())
                .priority(ticket.getPriority())
                .state(ticket.getState())
                .projectId(ticket.getProject() != null ? ticket.getProject().getId() : null)
                .build();
    }
}