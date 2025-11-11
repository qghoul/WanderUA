package com.khpi.wanderua.controller;
import com.khpi.wanderua.entity.Document;
import com.khpi.wanderua.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload multiple documents (temporary storage without linking to VerifyRequest)
     * Returns list of Document objects that can be attached to VerifyRequest
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadDocuments(@RequestParam("files") MultipartFile[] files) {
        log.info("Uploading {} documents", files.length);

        if (files == null || files.length == 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Не вибрано жодного файлу");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            List<Document> documents = documentService.uploadDocuments(files);

            log.info("Successfully uploaded {} documents", documents.size());
            return ResponseEntity.ok(documents);

        } catch (IllegalArgumentException e) {
            log.error("Invalid file upload request: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error uploading documents", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка завантаження файлів");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Download a specific document by ID
     */
    @GetMapping("/{documentId}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable Long documentId) {
        log.info("Downloading document with id: {}", documentId);

        try {
            Resource resource = documentService.loadDocumentAsResource(documentId);
            Document document = documentService.getDocumentById(documentId);

            String contentType = document.getFileType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);

        } catch (IllegalArgumentException e) {
            log.error("Document not found: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            log.error("Error downloading document", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка завантаження файлу");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get document metadata without downloading
     */
    @GetMapping("/{documentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDocumentInfo(@PathVariable Long documentId) {
        log.info("Getting info for document with id: {}", documentId);

        try {
            Document document = documentService.getDocumentById(documentId);

            Map<String, Object> info = new HashMap<>();
            info.put("id", document.getId());
            info.put("fileName", document.getFileName());
            info.put("fileType", document.getFileType());
            info.put("fileSize", documentService.getFileSize(document));

            return ResponseEntity.ok(info);

        } catch (IllegalArgumentException e) {
            log.error("Document not found: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            log.error("Error getting document info", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка отримання інформації про файл");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Delete a document (only if not attached to a verified request)
     */
    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        log.info("Deleting document with id: {}", documentId);

        try {
            documentService.deleteDocument(documentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Документ успішно видалено");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Cannot delete document: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error deleting document", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка видалення файлу");
            return ResponseEntity.status(500).body(error);
        }
    }
}
