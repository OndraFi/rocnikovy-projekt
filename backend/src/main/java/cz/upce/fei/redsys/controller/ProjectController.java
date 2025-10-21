package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.dto.ProjectDto.CreateProjectRequest;
import cz.upce.fei.redsys.dto.ProjectDto.ProjectResponse;
import cz.upce.fei.redsys.dto.ProjectDto.PaginatedProjectResponse;
import cz.upce.fei.redsys.dto.ProjectDto.UpdateProjectRequest;
import cz.upce.fei.redsys.service.ProjectService;
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
@RequestMapping(value = "/api/projects", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Manage projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create project", description = "Create a project. Name is required; description optional; status defaults to ACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest req) {
        ProjectResponse created = projectService.create(req);
        return ResponseEntity.created(URI.create("/api/projects/" + created.id())).body(created);
    }

    @Operation(summary = "List projects", description = "List projects owned by the authenticated user with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects found", content = @Content(schema = @Schema(implementation = PaginatedProjectResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedProjectResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(projectService.list(pageable));
    }

    @Operation(summary = "Get project", description = "Get a project by number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project found", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "403", description = "Project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.get(id));
    }

    @Operation(summary = "Update project", description = "Update name, description, and status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateProjectRequest req) {
        return ResponseEntity.ok(projectService.update(id, req));
    }

    @Operation(summary = "Delete project", description = "Delete project and its tickets")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project deleted"),
            @ApiResponse(responseCode = "403", description = "Project is not owned by current user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}