package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.domain.Image;
import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ImageDto;
import cz.upce.fei.redsys.dto.ImageDto.ImageResponse;
import cz.upce.fei.redsys.dto.ImageDto.DownloadResource;
import cz.upce.fei.redsys.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cz.upce.fei.redsys.security.annotation.ImagePermissions.CanDeleteImage;
import cz.upce.fei.redsys.security.annotation.ImagePermissions.CanUploadImage;

import java.net.URI;

import static cz.upce.fei.redsys.dto.ImageDto.toResponse;

@RestController
@RequestMapping(value = "/api/images", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Images", description = "Manage article images")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "404", description = "Image not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "Upload image", description = "Upload a new image file", operationId = "uploadImage")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file - check size or format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CanUploadImage
    public ResponseEntity<ImageResponse> upload(@RequestParam("file") MultipartFile file) {
        log.debug("POST /api/images: uploading file {}", file.getOriginalFilename());
        Image image = imageService.upload(file);
        ImageResponse response = toResponse(image);
        return ResponseEntity.created(URI.create("/api/images/" + image.getId()))
                .body(response);
    }

    @Operation(summary = "Get image", description = "Download image by ID", operationId = "getImage")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image found")
    })
    @GetMapping(value = "/{fileName}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<Resource> get(@PathVariable String fileName) {
        log.debug("GET /api/images/{}", fileName);

        DownloadResource image = imageService.get(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .contentLength(image.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(image.originalFilename())
                        .build().toString())
                .body(image.resource());
    }

    @GetMapping
    public ResponseEntity<ImageDto.PaginatedImageResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/categories: {}", pageable);
        return ResponseEntity.ok(imageService.list(pageable));
    }

    @Operation(summary = "Delete image", description = "Delete an image", operationId = "deleteImage")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image deleted")
    })
    @DeleteMapping("/{fileName}")
    @CanDeleteImage
    public ResponseEntity<Void> delete(@PathVariable String fileName) {
        log.debug("DELETE /api/images/{}", fileName);
        imageService.delete(fileName);
        return ResponseEntity.noContent().build();
    }
}