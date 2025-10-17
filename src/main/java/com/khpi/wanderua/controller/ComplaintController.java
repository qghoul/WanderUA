package com.khpi.wanderua.controller;

import com.khpi.wanderua.dto.*;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.service.ComplaintService;
import com.khpi.wanderua.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {
    private final ComplaintService complaintService;
    private final UserService userService;

    @PostMapping("/advertisement/{advertisementId}")
    public ResponseEntity<?> reportAdvertisement(
            @PathVariable Long advertisementId,
            @Valid @RequestBody ComplaintRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Необхідно увійти в систему");
            return ResponseEntity.status(401).body(error);
        }

        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Користувача не знайдено");
                return ResponseEntity.status(404).body(error);
            }

            Long userId = user.getId();
            AdvertisementComplaintResponse response = complaintService.createAdvertisementComplaint(
                    advertisementId, userId, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Скаргу успішно подано");
            result.put("complaint", response);

            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating advertisement complaint", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка створення скарги");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/advertisement")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdvertisementComplaintResponse>> getAllAdvertisementComplaints() {
        return ResponseEntity.ok(complaintService.getAllAdvertisementComplaints());
    }

    @GetMapping("/advertisement/unresolved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdvertisementComplaintResponse>> getUnresolvedAdvertisementComplaints() {
        return ResponseEntity.ok(complaintService.getUnresolvedAdvertisementComplaints());
    }

    @PostMapping("/review/{reviewId}")
    public ResponseEntity<?> reportReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ComplaintRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Необхідно увійти в систему");
            return ResponseEntity.status(401).body(error);
        }

        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Користувача не знайдено");
                return ResponseEntity.status(404).body(error);
            }

            Long userId = user.getId();
            ReviewComplaintResponse response = complaintService.createReviewComplaint(
                    reviewId, userId, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Скаргу успішно подано");
            result.put("complaint", response);

            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating review complaint", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка створення скарги");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewComplaintResponse>> getAllReviewComplaints() {
        return ResponseEntity.ok(complaintService.getAllReviewComplaints());
    }

    @GetMapping("/review/unresolved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewComplaintResponse>> getUnresolvedReviewComplaints() {
        return ResponseEntity.ok(complaintService.getUnresolvedReviewComplaints());
    }

    @PostMapping("/advertisement/{complaintId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveAdvertisementComplaint(
            @PathVariable Long complaintId,
            @RequestBody ResolveComplaintRequest request) {

        try {
            AdvertisementComplaintResponse response = complaintService.resolveAdvertisementComplaint(
                    complaintId, request.isConfirmed(), request.getAdminComment());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Скаргу успішно розглянуто");
            result.put("complaint", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error resolving advertisement complaint", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка розгляду скарги");
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/review/{complaintId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveReviewComplaint(
            @PathVariable Long complaintId,
            @RequestBody ResolveComplaintRequest request) {

        try {
            ReviewComplaintResponse response = complaintService.resolveReviewComplaint(
                    complaintId, request.isConfirmed(), request.getAdminComment());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Скаргу успішно розглянуто");
            result.put("complaint", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error resolving review complaint", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка розгляду скарги");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/advertisement/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdvertisementComplaintResponse>> getAdvertisementComplaintsByStatus(
            @RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(complaintService.getAdvertisementComplaintsByStatus(status));
    }

    @GetMapping("/review/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewComplaintResponse>> getReviewComplaintsByStatus(
            @RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(complaintService.getReviewComplaintsByStatus(status));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyComplaints(Authentication authentication){
        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Користувача не знайдено"));
            }
            Long userId = user.getId();
            log.info("Fetching complaints for user ID: {}", userId);

            UserComplaintsResponse complaints = complaintService.getUserComplaints(userId);
            return ResponseEntity.ok(complaints);

        } catch (Exception e) {
            log.error("Error fetching user complaints", e);
            return ResponseEntity.status(500).body(Map.of("error", "Помилка завантаження скарг"));
        }
    }

    @GetMapping("/my/advertisement")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AdvertisementComplaintResponse>> getMyAdvertisementComplaints(
            Authentication authentication) {

        User user = userService.getCurrentUser(authentication);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<AdvertisementComplaintResponse> complaints =
                complaintService.getAdvertisementComplaintsByUserId(user.getId());

        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/my/review")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewComplaintResponse>> getMyReviewComplaints(
            Authentication authentication) {

        User user = userService.getCurrentUser(authentication);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<ReviewComplaintResponse> complaints =
                complaintService.getReviewComplaintsByUserId(user.getId());

        return ResponseEntity.ok(complaints);
    }


}
