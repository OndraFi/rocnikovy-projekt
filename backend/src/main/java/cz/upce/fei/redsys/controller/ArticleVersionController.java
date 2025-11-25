package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleVersion;
import cz.upce.fei.redsys.dto.ArticleVersionDto;
import cz.upce.fei.redsys.dto.ArticleVersionDto.ArticleVersionResponse;
import cz.upce.fei.redsys.dto.ArticleVersionDto.PaginatedArticleVersionResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.service.ArticleService;
import cz.upce.fei.redsys.service.ArticleVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/articles/{articleId}/versions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Article Versions", description = "Manage article history")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Not allowed - insufficient role",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Article or version not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class ArticleVersionController {

    private final ArticleService articleService;
    private final ArticleVersionService articleVersionService;

    @Operation(summary = "List versions", description = "Get paginated history of versions for an article (without content).", operationId = "listArticleVersions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Versions found",
                    content = @Content(schema = @Schema(implementation = PaginatedArticleVersionResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedArticleVersionResponse> list(
            @PathVariable Long articleId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/articles/{}/versions: {}", articleId, pageable);
        Article article = articleService.requireArticleById(articleId);
        return ResponseEntity.ok(articleVersionService.listVersions(article, pageable));
    }

    @Operation(summary = "Get version", description = "Get a specific version of an article with content.", operationId = "getArticleVersion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Version found",
                    content = @Content(schema = @Schema(implementation = ArticleVersionResponse.class)))
    })
    @GetMapping("/{versionNumber}")
    public ResponseEntity<ArticleVersionResponse> get(
            @PathVariable Long articleId,
            @PathVariable Integer versionNumber) {
        log.debug("GET /api/articles/{}/versions/{}", articleId, versionNumber);
        Article article = articleService.requireArticleById(articleId);
        ArticleVersion version = articleVersionService.getVersion(article, versionNumber);
        return ResponseEntity.ok(ArticleVersionDto.toResponse(version, true));
    }
}