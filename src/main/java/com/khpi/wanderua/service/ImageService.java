package com.khpi.wanderua.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.nio.file.StandardCopyOption;


@Service
@Slf4j
public class ImageService {
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String saveReviewImage(MultipartFile file, Long reviewId) {
        validateImage(file);
        try {
            String filename = generateReviewImageName(reviewId);
            Path filePath = saveImageFile(file, filename, "reviews");

            log.info("Saved review image: {} for review ID: {}", filename, reviewId);
            return filename;
        } catch (
    IOException e) {
        log.error("Error saving review image for review ID: {}", reviewId, e);
        throw new RuntimeException("Помилка збереження зображення відгуку");
    }
    }

    public void deleteReviewImage(String filename) {
        deleteImageFile(filename, "reviews");
    }

    public Path getImagePath(String filename, String category) {
        return Paths.get(uploadDir, category, filename);
    }

    public boolean imageExists(String filename, String category) {
        Path imagePath = getImagePath(filename, category);
        return Files.exists(imagePath);
    }

    public String getImageUrl(String filename, String category) {
        if (filename == null) return null;
        return "/images/" + category + "/" + filename;
    }

    // Private helper methods
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл зображення не може бути порожнім");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Розмір файлу не може перевищувати 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Некоректне ім'я файлу");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Дозволені тільки зображення форматів: " +
                    String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Check if it's actually an image by content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Файл повинен бути зображенням");
        }
    }
    private Path saveImageFile(MultipartFile file, String filename, String category) throws IOException {
        // Create category directory
        Path categoryPath = Paths.get(uploadDir, category);
        if (!Files.exists(categoryPath)) {
            Files.createDirectories(categoryPath);
        }

        Path filePath = categoryPath.resolve(filename);

        // Copy file with replacement if exists
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath;
    }
    private void deleteImageFile(String filename, String category) {
        try {
            Path imagePath = getImagePath(filename, category);
            if (Files.exists(imagePath)) {

                Files.delete(imagePath);
                log.info("Deleted {} image: {}", category, filename);
            } else {
                log.warn("Image file not found for deletion: {} in category {}", filename, category);
            }
        } catch (IOException e) {
            log.error("Error deleting {} image: {}", category, filename, e);
        }
    }

    private String generateReviewImageName(Long reviewId) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "review_" + reviewId + "_" + uuid + "_" + System.currentTimeMillis();
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    public List<String> saveReviewImages(List<MultipartFile> files, Long reviewId) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        if (files.size() > 5) {
            throw new IllegalArgumentException("Максимум 5 зображень на відгук");
        }

        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> saveReviewImage(file, reviewId))
                .toList();
    }

    public void deleteReviewImages(List<String> filenames) {
        if (filenames != null && !filenames.isEmpty()) {
            filenames.forEach(this::deleteReviewImage);
        }
    }

}
