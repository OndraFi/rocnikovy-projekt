package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.CategoryDto.CategoryResponse;
import cz.upce.fei.redsys.dto.CategoryDto.CreateCategoryRequest;
import cz.upce.fei.redsys.dto.CategoryDto.PaginatedCategoryResponse;
import cz.upce.fei.redsys.dto.CategoryDto.UpdateCategoryRequest;
import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.security.annotation.CategoryPermissions.CanManageCategory;
import cz.upce.fei.redsys.service.CategoryService;
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
@RequestMapping(value = "/api/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Manage article categories")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Category not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create category", description = "Create a new category.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not allowed - insufficient role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Category name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CanManageCategory
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest req) {
        log.debug("POST /api/categories: {}", req);
        CategoryResponse created = categoryService.create(req);
        return ResponseEntity.created(URI.create("/api/categories/" + created.id()))
                .body(created);
    }

    @Operation(summary = "Get category", description = "Get a category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> get(@PathVariable Long id) {
        log.debug("GET /api/categories/{}", id);
        return ResponseEntity.ok(categoryService.get(id));
    }

    @Operation(summary = "List categories", description = "List categories with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories found",
                    content = @Content(schema = @Schema(implementation = PaginatedCategoryResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedCategoryResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/categories: {}", pageable);
        return ResponseEntity.ok(categoryService.list(pageable));
    }

    @Operation(summary = "Update category", description = "Update category fields")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not allowed - insufficient role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Category name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CanManageCategory
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest req) {
        log.debug("PUT /api/categories/{}: {}", id, req);
        return ResponseEntity.ok(categoryService.update(id, req));
    }

    @Operation(summary = "Delete category", description = "Delete a category")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "403", description = "Not allowed - insufficient role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    @CanManageCategory
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/categories/{}", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}