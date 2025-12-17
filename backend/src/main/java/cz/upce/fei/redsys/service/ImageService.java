package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Image;
import cz.upce.fei.redsys.dto.ImageDto;
import cz.upce.fei.redsys.dto.ImageDto.ImageResponse;
import cz.upce.fei.redsys.dto.ImageDto.DownloadResource;
import cz.upce.fei.redsys.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import cz.upce.fei.redsys.dto.ImageDto.PaginatedImageResponse;


@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/webp"
    };

    @Transactional
    public Image upload(MultipartFile file) {
        log.debug("Uploading image: {}", file.getOriginalFilename());

        validateFile(file);

        String filename = generateFilename(file.getOriginalFilename());
        String path = fileStorageService.store(file, filename);

        Image image = Image.builder()
                .filename(filename)
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .path(path)
                .build();
        image = imageRepository.save(image);
        log.debug("Image uploaded: id={}, filename={}", image.getId(), image.getFilename());

        return image;
    }

    @Transactional(readOnly = true)
    public DownloadResource get(String fileName) {
        log.debug("Getting image with name {}", fileName);
        Image image = imageRepository.findImageByFilename(fileName)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        log.debug("Image found: name={}", image.getFilename());

        Resource resource = fileStorageService.load(image.getFilename());

        return new DownloadResource(
                resource,
                image.getContentType(),
                image.getFileSize(),
                image.getOriginalFilename()
        );
    }

    @Transactional
    public void delete(String fileName) {
        log.debug("Deleting image with name {}", fileName);
        if (!imageRepository.existsByFilename(fileName)) {
            throw new EntityNotFoundException("Image not found");
        }
        imageRepository.deleteByFilename(fileName);
        fileStorageService.delete(fileName);

        log.debug("Image deleted: name={}", fileName);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5MB");
        }

        String contentType = file.getContentType();
        boolean isAllowed = contentType != null &&
                Arrays.stream(ALLOWED_CONTENT_TYPES).anyMatch(contentType::startsWith);

        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }
    }

    private String generateFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    @Transactional(readOnly = true)
    public PaginatedImageResponse list(Pageable pageable) {
        log.debug("Listing categories: {}", pageable);
        Page<Image> page = imageRepository.findAll(pageable);
        List<ImageResponse> images = page.getContent().stream()
                .map(ImageDto::toResponse)
                .toList();

        return new PaginatedImageResponse(
                images,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}