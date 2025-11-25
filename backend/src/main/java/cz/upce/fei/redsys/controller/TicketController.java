package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.dto.TicketDto.TransitionTicketRequest;
import cz.upce.fei.redsys.dto.TicketDto.CreateTicketRequest;
import cz.upce.fei.redsys.dto.TicketDto.TicketResponse;
import cz.upce.fei.redsys.dto.TicketDto.PaginatedTicketResponse;
import cz.upce.fei.redsys.dto.TicketDto.UpdateTicketRequest;
import cz.upce.fei.redsys.security.annotation.TicketPermissions.CanManageTicket;
import cz.upce.fei.redsys.security.annotation.TicketPermissions.CanViewTicket;
import cz.upce.fei.redsys.security.annotation.TicketPermissions.CanTransitionTicketState;
import cz.upce.fei.redsys.service.TicketService;
import cz.upce.fei.redsys.service.WorkflowService;
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
@RequestMapping(value = "/api/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tickets", description = "Manage tickets")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not allowed - insufficient role",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class TicketController {

    private final TicketService ticketService;
    private final WorkflowService workflowService;

    @Operation(summary = "Create ticket", description = "Create a new ticket", operationId = "createTicket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket created",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CanManageTicket
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        log.debug("POST /api/tickets: {}", req);
        TicketResponse created = ticketService.create(req);
        return ResponseEntity.created(URI.create("/tickets/" + created.id()))
                .body(created);
    }

    @Operation(summary = "List tickets", description = "List tickets with pagination", operationId = "listTickets")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets found",
                    content = @Content(schema = @Schema(implementation = PaginatedTicketResponse.class)))
    })
    @GetMapping
    @CanViewTicket
    public ResponseEntity<PaginatedTicketResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/tickets: {}", pageable);
        return ResponseEntity.ok(ticketService.list(pageable));
    }

    @Operation(summary = "Get ticket", description = "Get a ticket by ID", operationId = "getTicket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket found",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class)))
    })
    @GetMapping("/{ticketId}")
    @CanViewTicket
    public ResponseEntity<TicketResponse> get(@PathVariable Long ticketId) {
        log.debug("GET /api/tickets/{}", ticketId);
        return ResponseEntity.ok(ticketService.get(ticketId));
    }

    @Operation(summary = "Update ticket", description = "Update ticket fields", operationId = "updateTicket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket updated",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PutMapping(value = "/{ticketId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CanManageTicket
    public ResponseEntity<TicketResponse> update(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketRequest req) {
        log.debug("PUT /api/tickets/{}: {}", ticketId, req);
        return ResponseEntity.ok(ticketService.update(ticketId, req));
    }

    @Operation(summary = "Transition ticket state", description = "Change the state of a ticket", operationId = "changeTicketState")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket state transitioned",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not allowed - insufficient role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Invalid state transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{ticketId}/state")
    @CanTransitionTicketState
    public ResponseEntity<TicketResponse> changeState(
            @PathVariable Long ticketId,
            @Valid @RequestBody TransitionTicketRequest request) {

        log.debug("POST /api/tickets/{}/state: {}", ticketId, request);
        return ResponseEntity.ok(workflowService.transition(ticketId, request.targetState(), request.comment()));
    }

    @Operation(summary = "Delete ticket", description = "Delete a ticket", operationId = "deleteTicket")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ticket deleted")
    })
    @DeleteMapping("/{ticketId}")
    @CanManageTicket
    public ResponseEntity<Void> delete(@PathVariable Long ticketId) {
        log.debug("DELETE /api/tickets/{}", ticketId);
        ticketService.delete(ticketId);
        return ResponseEntity.noContent().build();
    }
}