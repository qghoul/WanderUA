package com.khpi.wanderua.controller;

import com.khpi.wanderua.service.ImageService;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/images")
@RequiredArgsConstructor
public class StaticResourceController {
    private final ImageService imageService;

    private static final Map<String, MediaType> MEDIA_TYPES = Map.of(
            "jpg", MediaType.IMAGE_JPEG,
            "jpeg", MediaType.IMAGE_JPEG,
            "png", MediaType.IMAGE_PNG,
            "gif", MediaType.IMAGE_GIF,
            "webp", MediaType.valueOf("image/webp")
    );
    @GetMapping("/reviews/{filename}")
    public ResponseEntity<Resource> getReviewImage(@PathVariable String filename) {
        return getImage(filename, "reviews");
    }

    private ResponseEntity<Resource> getImage(String filename, String category) {
        try {
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                log.warn("Invalid filename attempted: {}", filename);
                return ResponseEntity.badRequest().build();
            }

            if (!imageService.imageExists(filename, category)) {
                log.debug("Image not found: {} in category: {}", filename, category);
                return ResponseEntity.notFound().build();
            }

            Path imagePath = imageService.getImagePath(filename, category);
            Resource resource = new FileSystemResource(imagePath);

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("Image exists in system but not readable: {}", imagePath);
                return ResponseEntity.notFound().build();
            }

            MediaType mediaType = getMediaType(filename);

            // Set cache headers for better performance
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setCacheControl("public, max-age=3600"); // Cache 1 hour

            log.debug("Serving image: {} with type: {}", filename, mediaType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (RuntimeException e) {
            log.error("Error serving image: {} in category: {}", filename, category, e);
            throw e;
        }
    }

    private MediaType getMediaType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return MEDIA_TYPES.getOrDefault(extension, MediaType.APPLICATION_OCTET_STREAM);
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

}
