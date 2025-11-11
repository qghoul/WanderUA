package com.khpi.wanderua.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Create upload directories on application startup
     */
    @PostConstruct
    public void init() {
        try {
            // Create main upload directory
            Path mainUploadPath = Paths.get(uploadDir);
            if (!Files.exists(mainUploadPath)) {
                Files.createDirectories(mainUploadPath);
            }

            // Create documents subdirectory
            Path documentsPath = Paths.get(uploadDir, "documents");
            if (!Files.exists(documentsPath)) {
                Files.createDirectories(documentsPath);
            }

            System.out.println("✓ Upload directories initialized at: " + mainUploadPath.toAbsolutePath());
            System.out.println("  - Documents: " + documentsPath.toAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }
}