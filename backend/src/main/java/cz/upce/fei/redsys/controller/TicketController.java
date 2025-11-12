package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.dto.TicketDto.CreateTicketRequest;
import cz.upce.fei.redsys.dto.TicketDto.TicketResponse;
import cz.upce.fei.redsys.dto.TicketDto.PaginatedTicketResponse;
import cz.upce.fei.redsys.dto.TicketDto.UpdateTicketRequest;
import cz.upce.fei.redsys.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Manage tickets")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Create ticket", description = "Create a new ticket. Title, assignee, author, and article are required; state defaults to OPEN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket created",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        TicketResponse created = ticketService.create(req);
        return ResponseEntity.created(URI.create("/tickets/" + created.id()))
                .body(created);
    }

    @Operation(summary = "List tickets", description = "List tickets with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets found",
                    content = @Content(schema = @Schema(implementation = PaginatedTicketResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedTicketResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ticketService.list(pageable));
    }

    @Operation(summary = "Get ticket", description = "Get a ticket by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket found",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class)))
    })
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.get(ticketId));
    }

    @Operation(summary = "Update ticket", description = "Update ticket fields")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket updated",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PutMapping(value = "/{ticketId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketResponse> update(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketRequest req) {
        return ResponseEntity.ok(ticketService.update(ticketId, req));
    }

    @Operation(summary = "Delete ticket", description = "Delete a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ticket deleted")
    })
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> delete(@PathVariable Long ticketId) {
        ticketService.delete(ticketId);
        return ResponseEntity.noContent().build();
    }
}