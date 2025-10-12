package com.khpi.wanderua.controller;

import com.khpi.wanderua.dto.ReviewDTO;
import com.khpi.wanderua.entity.Review;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.enums.GoWithOptions;
import com.khpi.wanderua.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/advertisement/{advertisementId}") //used now for review display
    public ResponseEntity<List<ReviewDTO>> getReviewsByAdvertisement(
            @PathVariable Long advertisementId,
            @RequestParam(defaultValue = "useful") String sort) {

        List<ReviewDTO> reviews;
        if("date".equals(sort)){
            reviews  = reviewService.getReviewsDTOByAdvertisementIdSortedByDate(advertisementId, getCurrentUserId());
        }
        else {
            reviews = reviewService.getReviewsDTOByAdvertisementId(advertisementId, getCurrentUserId());
        }
        return ResponseEntity.ok(reviews);
    }
    @GetMapping("/advertisement/{advertisementId}/paged") //need to update front-end to implement pageable review display
    public ResponseEntity<Page<ReviewDTO>> getReviewsByAdvertisementPaged(
            @PathVariable Long advertisementId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getReviewsDTOByAdvertisementId(advertisementId, getCurrentUserId(), pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReview(
            @RequestParam("advertisementId") Long advertisementId,
            @RequestParam("rating") Integer rating,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "goWith", required = false) String goWithStr,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpSession session){

        log.info("=== CREATE REVIEW REQUEST STARTED ===");
        log.info("Advertisement ID: {}", advertisementId);
        log.info("Rating: {}", rating);
        log.info("Title: {}", title);
        log.info("Comment: {}", comment);
        log.info("GoWith: {}", goWithStr);
        log.info("Images count: {}", images != null ? images.size() : 0);

        try {
            GoWithOptions goWith = null;
            if (goWithStr != null && !goWithStr.isEmpty()) {
                log.info("Parsing GoWithOptions from: {}", goWithStr);
                goWith = GoWithOptions.valueOf(goWithStr);
            }

            Long userId = getCurrentUserId();
            log.info("Current user ID: {}", userId);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Необхідно увійти в систему"));
            }

            ReviewDTO reviewDTO = ReviewDTO.builder()
                    .advertisementId(advertisementId)
                    .rating(rating)
                    .title(title)
                    .comment(comment)
                    .goWith(goWith)
                    .build();

            log.info("ReviewDTO created: {}", reviewDTO);

            ReviewDTO createdReview = reviewService.createReview(reviewDTO, userId, images);

            log.info("Review created successfully with ID: {}", createdReview.getId());
            return ResponseEntity.ok(createdReview);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("State error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating review", e);
            log.error("Error type: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Помилка створення відгуку: " + e.getMessage()));
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long reviewId) {
        try {
            ReviewDTO review = reviewService.getReviewById(reviewId, getCurrentUserId());
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        try {
            Long userId = getCurrentUserId();
            if(userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Необхідно увійти в систему"));
            }

            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok(Map.of("message", "Відгук видалено"));

        } catch (IllegalStateException e) {
            log.warn("Authorization error: {}", e.getMessage());
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting review", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Помилка видалення відгуку: " + e.getMessage()));
        }
    }

    @PostMapping("/{reviewId}/useful")
    public ResponseEntity<?> markAsUseful(@PathVariable Long reviewId) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Необхідно увійти в систему"));
            }

            reviewService.markReviewAsUseful(reviewId, userId);
            return ResponseEntity.ok(Map.of("message", "Відгук відмічено як корисний"));
        } catch (IllegalStateException e) {
            log.warn("State error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error marking review as useful", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Помилка оновлення відгуку: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{reviewId}/useful")
    public ResponseEntity<?> unmarkReviewAsUseful(@PathVariable Long reviewId) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Необхідно увійти в систему"));
            }

            reviewService.unmarkReviewAsUseful(reviewId, userId);
            return ResponseEntity.ok(Map.of("message", "Відмітку корисності знято"));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error unmarking review as useful", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Помилка оновлення відгуку"));
        }
    }

    @GetMapping("/{reviewId}/useful/status")
    public ResponseEntity<?> getUsefulStatus(@PathVariable Long reviewId) {
        try {
            Long userId = getCurrentUserId();

            if (userId == null) {
                return ResponseEntity.ok(Map.of("markedAsUseful", false));
            }

            boolean marked = reviewService.hasUserMarkedAsUseful(reviewId, userId);
            return ResponseEntity.ok(Map.of("markedAsUseful", marked));

        } catch (Exception e) {
            log.error("Error getting useful status", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Помилка"));
        }
    }

    @GetMapping("/advertisement/{advertisementId}/useful-status")
    public ResponseEntity<?> getUsefulStatusForAdvertisement(@PathVariable Long advertisementId) {
        log.info("Getting useful statuses for advertisement {}", advertisementId);
        try {
            Long userId = getCurrentUserId();

            if (userId == null) {
                log.info("User not authenticated, returning empty statuses");
                return ResponseEntity.ok(Map.of("statuses", Map.of()));
            }

            List<Review> reviews = reviewService.getReviewsByAdvertisementId(advertisementId);
            List<Long> reviewIds = reviews.stream()
                    .map(Review::getId)
                    .collect(Collectors.toList());

            Map<Long, Boolean> statuses = reviewService.getUserUsefulStatusForReviews(reviewIds, userId);

            log.info("Returning statuses for {} reviews", statuses.size());
            return ResponseEntity.ok(Map.of("statuses", statuses));

        } catch (Exception e) {
            log.error("Error getting useful statuses", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Помилка"));
        }
    }

    @GetMapping("/can-review/{advertisementId}")
    public ResponseEntity<Map<String, Object>> canUserReview(
            @PathVariable Long advertisementId) {

        try {
            Long userId = getCurrentUserId();

            if (userId == null) {
                return ResponseEntity.ok(Map.of(
                        "canReview", false,
                        "needLogin", true,
                        "message", "Необхідно увійти в систему"
                ));
            }

            boolean canReview = reviewService.canUserReview(userId, advertisementId);

            return ResponseEntity.ok(Map.of(
                    "canReview", canReview,
                    "needLogin", false,
                    "message", canReview ? "Можна залишити відгук" : "Ви вже залишили відгук"
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "canReview", false,
                    "needLogin", false,
                    "message", "Помилка перевірки дозволів"
            ));
        }
    }
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails) {
                // Получить userId из UserDetails
                return ((User) principal).getId();
            }
        }
        return null;
    }
}
