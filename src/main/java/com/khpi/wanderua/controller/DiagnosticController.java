package com.khpi.wanderua.controller;

import com.khpi.wanderua.entity.Document;
import com.khpi.wanderua.entity.VerifyRequest;
import com.khpi.wanderua.repository.DocumentRepository;
import com.khpi.wanderua.repository.VerifyRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ТИМЧАСОВИЙ контролер для діагностики проблеми з документами
 * Видаліть після виправлення
 */
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class DiagnosticController {

    private final VerifyRequestRepository verifyRequestRepository;
    private final DocumentRepository documentRepository;

    @GetMapping("/verify-request/{id}")
    public Map<String, Object> debugVerifyRequest(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        log.info("=== DEBUG: Checking verify request {} ===", id);

        // Завантажуємо verify request
        VerifyRequest verifyRequest = verifyRequestRepository.findById(id)
                .orElse(null);

        if (verifyRequest == null) {
            result.put("error", "VerifyRequest not found");
            return result;
        }

        result.put("verifyRequestId", verifyRequest.getId());
        result.put("type", verifyRequest.getType());
        result.put("resolved", verifyRequest.isResolved());

        // Спроба 1: Прямий доступ до documents
        try {
            Set<Document> documents = verifyRequest.getDocuments();
            log.info("Documents collection class: {}",
                    documents != null ? documents.getClass().getName() : "null");

            if (documents != null) {
                log.info("Documents size (before init): {}", documents.size());
                int count = documents.size(); // Ініціалізує lazy collection
                result.put("documentsCount", count);
                result.put("documentsLoaded", true);

                // Перевіряємо кожен документ
                for (Document doc : documents) {
                    log.info("Document: id={}, fileName={}", doc.getId(), doc.getFileName());
                }

                result.put("documents", documents);
            } else {
                result.put("documentsCount", 0);
                result.put("documentsLoaded", false);
                result.put("error", "Documents collection is null");
            }
        } catch (Exception e) {
            log.error("Error accessing documents", e);
            result.put("error", "Exception: " + e.getMessage());
        }

        // Спроба 2: Пряме завантаження через DocumentRepository
        List<Document> directDocuments = documentRepository.findByVerifyRequestId(id);
        result.put("directDocumentsCount", directDocuments.size());
        result.put("directDocuments", directDocuments);

        log.info("=== DEBUG RESULT: {} ===", result);

        return result;
    }

    @GetMapping("/all-documents")
    public Map<String, Object> debugAllDocuments() {
        Map<String, Object> result = new HashMap<>();

        List<Document> allDocuments = documentRepository.findAll();
        result.put("totalDocuments", allDocuments.size());

        // Групуємо по verify request
        Map<String, Integer> byVerifyRequest = new HashMap<>();
        for (Document doc : allDocuments) {
            String key = doc.getVerifyRequest() != null
                    ? "VR_" + doc.getVerifyRequest().getId()
                    : "NULL";
            byVerifyRequest.put(key, byVerifyRequest.getOrDefault(key, 0) + 1);
        }

        result.put("documentsByVerifyRequest", byVerifyRequest);

        return result;
    }
}