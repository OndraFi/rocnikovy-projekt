package cz.upce.fei.redsys.dto;

import lombok.Builder;
import cz.upce.fei.redsys.domain.TicketComment;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class TicketCommentDto {
    private TicketCommentDto() {}

    @Builder
    public record TicketCommentResponse(
            Integer number,
            String content,
            Instant createdAt,
            Instant updatedAt,
            UserResponse author
    ) {}

    public record PaginatedTicketCommentResponse(
            List<TicketCommentResponse> comments,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record CreateTicketCommentRequest(
            @NotBlank(message = "{common.required}")
            @Size(max = 2000, message = "{ticket.comment.size}")
            String content
    ) {}

    public record UpdateTicketCommentRequest(
            @NotBlank(message = "{common.required}")
            @Size(max = 2000, message = "{ticket.comment.size}")
            String content
    ) {}

    public static TicketCommentResponse toResponse(TicketComment comment) {
        return TicketCommentResponse.builder()
                .number(comment.getTicketCommentNumber())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(UserDto.toUserResponse(comment.getAuthor()))
                .build();
    }
}
