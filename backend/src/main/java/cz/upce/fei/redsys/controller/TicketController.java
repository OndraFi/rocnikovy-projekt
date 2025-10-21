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
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
@RequestMapping(value = "/api/projects/{projectId}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Manage tickets within a project")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Create ticket", description = "Create a ticket in a project. Title, type, priority required; state defaults to OPEN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket created", content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketResponse> create(@PathVariable Long projectId,
                                                 @Valid @RequestBody CreateTicketRequest req) {
        TicketResponse created = ticketService.create(projectId, req);
        return ResponseEntity.created(URI.create("/api/projects/" + projectId + "/tickets/" + created.number()))
                .body(created);
    }

    @Operation(summary = "List tickets", description = "List tickets for a project with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets found", content = @Content(schema = @Schema(implementation = PaginatedTicketResponse.class))),
            @ApiResponse(responseCode = "403", description = "Project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedTicketResponse> list(@PathVariable Long projectId,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ticketService.list(projectId, pageable));
    }

    @Operation(summary = "Get ticket", description = "Get a ticket by number within the given project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket found", content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "403", description = "Ticket/project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket/project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{ticketNumber}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long projectId,
                                              @PathVariable Long ticketNumber) {
        return ResponseEntity.ok(ticketService.get(projectId, ticketNumber));
    }

    @Operation(summary = "Update ticket", description = "Update ticket fields within the given project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket updated", content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Ticket/project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket/project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{ticketNumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketResponse> update(@PathVariable Long projectId,
                                                 @PathVariable Long ticketNumber,
                                                 @Valid @RequestBody UpdateTicketRequest req) {
        return ResponseEntity.ok(ticketService.update(projectId, ticketNumber, req));
    }

    @Operation(summary = "Delete ticket", description = "Delete a ticket within the given project")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ticket deleted"),
            @ApiResponse(responseCode = "403", description = "Ticket/project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket/project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{ticketNumber}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                       @PathVariable Long ticketNumber) {
        ticketService.delete(projectId, ticketNumber);
        return ResponseEntity.noContent().build();
    }
}