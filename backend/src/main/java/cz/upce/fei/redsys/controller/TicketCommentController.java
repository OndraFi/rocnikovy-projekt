package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.PaginatedTicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.CreateTicketCommentRequest;
import cz.upce.fei.redsys.dto.TicketCommentDto.TicketCommentResponse;
import cz.upce.fei.redsys.service.TicketCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/tickets/{ticketId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ticket Comments", description = "Manage comments for tickets")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;

    @Operation(summary = "List comments", description = "Get paginated comments for a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments found", content = @Content(schema = @Schema(implementation = PaginatedTicketCommentResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedTicketCommentResponse> list(
            @PathVariable Long ticketId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/tickets/{}/comments: {}", ticketId, pageable);
        return ResponseEntity.ok(ticketCommentService.listComments(ticketId, pageable));
    }

    @Operation(summary = "Create comment", description = "Add a comment to a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment created", content = @Content(schema = @Schema(implementation = TicketCommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketCommentResponse> create(
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateTicketCommentRequest req) {
        log.debug("POST /api/tickets/{}/comments: {}", ticketId, req);
        TicketCommentResponse created = ticketCommentService.createComment(ticketId, req);
        return ResponseEntity.created(URI.create("/tickets/" + ticketId + "/comments/" + created.number()))
                .body(created);
    }
}
