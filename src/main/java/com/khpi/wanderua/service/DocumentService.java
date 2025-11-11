package com.khpi.wanderua.service;

import com.khpi.wanderua.entity.Document;
import com.khpi.wanderua.entity.VerifyRequest;
import com.khpi.wanderua.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Allowed file extensions
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "jpg", "jpeg", "png", "doc", "docx"
    );

    // Max file size: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * Upload multiple documents and save them temporarily
     */
    @Transactional
    public List<Document> uploadDocuments(MultipartFile[] files) {
        List<Document> documents = new ArrayList<>();

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, "documents");
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }
        } catch (IOException e) {
            log.error("Failed to create upload directory", e);
            throw new RuntimeException("Не вдалося створити директорію для завантаження");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // Validate file
            validateFile(file);

            try {
                // Generate unique filename
                String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String fileExtension = getFileExtension(originalFilename);
                String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

                // Save file to disk
                Path targetLocation = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                // Create Document entity
                Document document = Document.builder()
                        .fileName(originalFilename)
                        .fileType(file.getContentType())
                        .filePath(targetLocation.toString())
                        .build();

                // Save to database
                Document savedDocument = documentRepository.save(document);
                documents.add(savedDocument);

                log.info("Uploaded document: {} -> {}", originalFilename, uniqueFilename);

            } catch (IOException e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                throw new RuntimeException("Помилка збереження файлу: " + file.getOriginalFilename());
            }
        }

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Не вдалося завантажити жодного файлу");
        }

        return documents;
    }

    /**
     * Load document as Resource for downloading
     */
    @Transactional(readOnly = true)
    public Resource loadDocumentAsResource(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Документ не знайдено"));

        try {
            Path filePath = Paths.get(document.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не знайдено або не може бути прочитаний");
            }
        } catch (MalformedURLException e) {
            log.error("Invalid file path for document {}: {}", documentId, document.getFilePath(), e);
            throw new RuntimeException("Невірний шлях до файлу");
        }
    }

    /**
     * Get document by ID
     */
    @Transactional(readOnly = true)
    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Документ не знайдено"));
    }

    /**
     * Get file size in bytes
     */
    public long getFileSize(Document document) {
        try {
            Path filePath = Paths.get(document.getFilePath());
            return Files.size(filePath);
        } catch (IOException e) {
            log.error("Failed to get file size for document {}", document.getId(), e);
            return 0;
        }
    }

    /**
     * Delete document and its file
     */
    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Документ не знайдено"));

        // Check if document is attached to a verify request
        if (document.getVerifyRequest() != null && document.getVerifyRequest().getConfirmed() != null) {
            throw new IllegalArgumentException("Не можна видалити документ, прикріплений до обробленого запиту");
        }

        // Delete file from disk
        try {
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);
            log.info("Deleted file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file for document {}", documentId, e);
        }

        // Delete from database
        documentRepository.delete(document);
        log.info("Deleted document from database: {}", documentId);
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл порожній");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Файл занадто великий: " + file.getOriginalFilename() +
                            ". Максимальний розмір: 10MB"
            );
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Невірне ім'я файлу");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Недозволений тип файлу: " + extension +
                            ". Дозволені: PDF, JPG, JPEG, PNG, DOC, DOCX"
            );
        }

        // Check for path traversal
        if (filename.contains("..")) {
            throw new IllegalArgumentException("Невірне ім'я файлу");
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * Link documents to a verify request
     */
    @Transactional
    public void linkDocumentsToVerifyRequest(Set<Long> documentIds, VerifyRequest verifyRequest) {
        if (documentIds == null || documentIds.isEmpty()) {
            return;
        }

        for (Long documentId : documentIds) {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Документ не знайдено: " + documentId));

            document.setVerifyRequest(verifyRequest);
            documentRepository.save(document);
        }

        log.info("Linked {} documents to verify request {}", documentIds.size(), verifyRequest.getId());
    }

    /**
     * Get documents by verify request ID
     */
    @Transactional(readOnly = true)
    public Set<Document> getDocumentsByVerifyRequestId(Long verifyRequestId) {
        return new HashSet<>(documentRepository.findByVerifyRequestId(verifyRequestId));
    }

    /**
     * Delete all documents for a verify request
     */
    @Transactional
    public void deleteDocumentsForVerifyRequest(Long verifyRequestId) {
        Set<Document> documents = getDocumentsByVerifyRequestId(verifyRequestId);

        for (Document document : documents) {
            try {
                Path filePath = Paths.get(document.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("Failed to delete file for document {}", document.getId(), e);
            }
        }

        documentRepository.deleteAll(documents);
        log.info("Deleted {} documents for verify request {}", documents.size(), verifyRequestId);
    }
}