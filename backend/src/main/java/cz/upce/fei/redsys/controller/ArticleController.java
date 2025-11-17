package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.ArticleDto.*;
import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/api/articles", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Articles", description = "Manage articles")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Article not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "Create article", description = "Create a new article. Title is required; state defaults to DRAFT")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Article created",
                    content = @Content(schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleResponse> create(@Valid @RequestBody CreateArticleRequest req) {
        log.debug("POST /api/articles: {}", req);
        ArticleResponse created = articleService.create(req);
        return ResponseEntity.created(URI.create("/articles/" + created.id()))
                .body(created);
    }

    @Operation(summary = "Get article", description = "Get an article by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article found",
                    content = @Content(schema = @Schema(implementation = ArticleResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> get(@PathVariable Long id) {
        log.debug("GET /api/articles/{}", id);
        return ResponseEntity.ok(articleService.get(id));
    }

    @Operation(summary = "List articles", description = "List articles with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Articles found",
                    content = @Content(schema = @Schema(implementation = PaginatedArticleResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedArticleResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/articles: {}", pageable);
        return ResponseEntity.ok(articleService.list(pageable));
    }

    @Operation(summary = "Update article", description = "Update article fields")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article updated",
                    content = @Content(schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateArticleRequest req) {
        log.debug("PUT /api/articles/{}: {}", id, req);
        return ResponseEntity.ok(articleService.update(id, req));
    }

    @Operation(summary = "Delete article", description = "Delete an article")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Article deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/articles/{}", id);
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
