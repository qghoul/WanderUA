package com.khpi.wanderua.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khpi.wanderua.config.SecurityHelper;
import com.khpi.wanderua.dto.*;
import com.khpi.wanderua.dto.AdvertisementDetailResponse;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.service.AdvertisementService;
import com.khpi.wanderua.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/advertisements")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final UserService userService;
    private final SecurityHelper securityHelper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_BUSINESS')")
    public ResponseEntity<?> createAdvertisement(
            @RequestParam("data") String requestJson,
            @RequestParam(value = "images", required = true) List<MultipartFile> images,
            Authentication authentication) {

        try {
            if (!securityHelper.canCreateAdvertisement(authentication)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Доступ заборонений");
                error.put("message", "Тільки верифіковані представники бізнесу можуть створювати оголошення");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            // parse into Map to check type of advert
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> rawData = objectMapper.readValue(requestJson, Map.class);

            String advertisementType = (String) rawData.get("advertisementType");
            log.info("Advertisement type: {}", advertisementType);

            if (advertisementType == null) {
                throw new ValidationException("Поле advertisementType є обов'язковим");
            }

            // Create DTO for current type
            CreateAdvertisementRequest request;
            switch (advertisementType) {
                case "RESTAURANT":
                    request = objectMapper.readValue(requestJson, CreateRestaurantAdvertRequest.class);
                    break;
                case "TOUR":
                    request = objectMapper.readValue(requestJson, CreateTourAdvertRequest.class);
                    break;
                case "ACCOMODATION":
                    request = objectMapper.readValue(requestJson, CreateAccomodationAdvertRequest.class);
                    break;
                case "ENTERTAINMENT":
                    request = objectMapper.readValue(requestJson, CreateEntertainmentAdvertRequest.class);
                    break;
                case "PUBLIC_ATTRACTION":
                    request = objectMapper.readValue(requestJson, CreatePublicAttractionAdvertRequest.class);
                    break;
                default:
                    throw new ValidationException("Невідомий тип оголошення: " + advertisementType);
            }

            log.info("Successfully parsed request: {}", request.getClass().getSimpleName());

            Set<ConstraintViolation<CreateAdvertisementRequest>> violations =
                    Validation.buildDefaultValidatorFactory().getValidator().validate(request);

            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<CreateAdvertisementRequest> violation : violations) {
                    sb.append(violation.getMessage()).append("; ");
                }
                throw new ValidationException(sb.toString());
            }

            if (images == null || images.isEmpty()) {
                throw new ValidationException("Зображення є обов'язковими");
            }

            if (images.size() > 5) {
                throw new ValidationException("Максимум 5 зображень");
            }

            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    throw new ValidationException("Файл зображення не може бути порожнім");
                }
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new ValidationException("Файл повинен бути зображенням");
                }

                // img max 10MB
                if (image.getSize() > 10 * 1024 * 1024) {
                    throw new ValidationException("Розмір файлу не повинен перевищувати 10MB");
                }
            }

            String username = authentication.getName();
            AdvertisementResponse response = advertisementService.createAdvertisement(request, images, username);

            log.info("Advertisement created successfully by user: {}, id: {}", username, response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ValidationException e) {
            log.warn("Validation error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка валідації");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            log.error("Error creating advertisement: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка створення оголошення");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/catalog")
    public ResponseEntity<CatalogResponse> getCatalogAdvertisements(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "sortBy", defaultValue = "popular") String sortBy,
            @RequestParam(value = "permanentOnly", defaultValue = "false") Boolean sustainableOnly,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            log.info("Getting catalog advertisements for city: {}, category: {}, page: {}", city, category, page);

            if (page < 0 || size <= 0 || size > 100) {
                return ResponseEntity.badRequest().build();
            }
            CatalogFilterRequest filterRequest = new CatalogFilterRequest();
            filterRequest.setCity(city);
            filterRequest.setMinPrice(minPrice);
            filterRequest.setMaxPrice(maxPrice);
            filterRequest.setSortBy(sortBy);
            filterRequest.setSustainableOnly(sustainableOnly);
            filterRequest.setPage(page);
            filterRequest.setSize(size);

            if (category != null && !category.trim().isEmpty()) {
                try {
                    AdvertisementType advertisementType = AdvertisementType.valueOf(category.toUpperCase());
                    filterRequest.setCategory(advertisementType);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid category: {}", category);
                }
            }

            CatalogResponse response = advertisementService.getCatalogAdvertisements(filterRequest);

            log.info("Successfully retrieved {} advertisements for catalog", response.getAdvertisements().size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting catalog advertisements: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<AdvertisementResponse>> getAllAdvertisements(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            if (page < 0 || size <= 0 || size > 100) {
                return ResponseEntity.badRequest().build();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<AdvertisementResponse> advertisements = advertisementService.getAdvertisements(category, pageable);

            return ResponseEntity.ok(advertisements.getContent());

        } catch (Exception e) {
            log.error("Error getting advertisements: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDetailResponse> getAdvertisement(@PathVariable Long id) {
        log.info("API: Getting advertisement with ID: {}", id);

        try {
            if (id == null || id < 0) {
                log.error("API: Invalid ID provided: {}", id);
                return ResponseEntity.badRequest().build();
            }

            log.info("API: Calling service to get advertisement detail for ID: {}", id);
            AdvertisementDetailResponse advertisement = advertisementService.getAdvertisementDetailById(id);

            log.info("API: Successfully retrieved advertisement: {}", advertisement.getTitle());
            return ResponseEntity.ok(advertisement);

        } catch (EntityNotFoundException e) {
            log.error("API: Advertisement not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("API: Error getting advertisement by id {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_BUSINESS')")
    public ResponseEntity<?> updateAdvertisement(
            @PathVariable Long id,
            @RequestParam("data") String requestJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {

        try {
            if (!securityHelper.canCreateAdvertisement(authentication)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Доступ заборонений");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            if (!securityHelper.isAdvertisementOwner(id, authentication) &&
                    !securityHelper.isAdmin(authentication)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ви можете редагувати тільки свої оголошення");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Функція оновлення буде реалізована пізніше");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating advertisement: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка оновлення оголошення");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_BUSINESS', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteAdvertisement(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            if (!securityHelper.isAdvertisementOwner(id, authentication) &&
                    !securityHelper.isAdmin(authentication)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ви можете видаляти тільки свої оголошення");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Функція видалення буде реалізована пізніше");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting advertisement: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка видалення оголошення");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_BUSINESS')")
    public ResponseEntity<List<AdvertisementResponse>> getMyAdvertisements(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Authentication authentication) {

        try {
            User currentUser = userService.getCurrentUser(authentication);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<AdvertisementResponse> userAdvertisements =
                    advertisementService.getUserAdvertisements(currentUser.getId(), pageable);

            return ResponseEntity.ok(userAdvertisements.getContent());

        } catch (Exception e) {
            log.error("Error getting user advertisements: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Доступ заборонений");
        error.put("message", "У вас немає прав для виконання цієї операції");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
