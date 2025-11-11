package com.khpi.wanderua.controller;

import com.khpi.wanderua.dto.*;
import com.khpi.wanderua.entity.Business;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.service.BusinessRequestService;
import com.khpi.wanderua.service.UserService;
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
@RequestMapping("/api/verify-requests")
@RequiredArgsConstructor
@Slf4j
public class VerifyRequestController {
    private final BusinessRequestService businessRequestService;
    private final UserService userService;

    @PostMapping("/business-verify/send")
    public ResponseEntity<?> sendBusinessRepresentVerifyRequest(
            @RequestBody BusinessRepresentVerifyRequest request,
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
            BusinessRequestDTO response = businessRequestService
                    .createBusinessRepresentVerifyRequest(user.getId(), request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Запит на верифікацію як представник бізнесу подано");
            result.put("verify-request", response);

            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating business represent verify request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка створення запиту");
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sustainability-status/send")
    public ResponseEntity<?> sendSustainabilityStatusRequest(
            @RequestBody SustainabilityStatusRequest request,
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
            BusinessRequestDTO response = businessRequestService
                    .createSustainabilityStatusRequest(user.getId(), request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Запит на верифікацію бізнесу як сталий");
            result.put("verify-request", response);

            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating sustainability verify request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка створення запиту");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/business-represent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BusinessRequestDTO>> getAllBusinessRepresentVerifyRequests(){
        return ResponseEntity.ok(businessRequestService.getAllBusinessRepresentVerifyRequests());
    }
    @GetMapping("/sustainability-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BusinessRequestDTO>> getAllSustainabilityStatusRequests(){
        return ResponseEntity.ok(businessRequestService.getAllSustainabilityStatusRequests());
    }

    @GetMapping("/business-represent/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BusinessRequestDTO>> getBusinessRepresentVerifyRequestsByStatus(
            @RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(businessRequestService.getBusinessRepresentVerifyRequestsByStatus(status));
    }
    @GetMapping("/sustainability-status/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BusinessRequestDTO>> getSustainabilityStatusRequestsByStatus(
            @RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(businessRequestService.getSustainabilityStatusRequestsByStatus(status));
    }

    @PostMapping("/business-represent/{verifyRequestId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveBusinessRepresentVerifyRequest(
            @PathVariable Long verifyRequestId,
            @RequestBody ResolveBusinessRequestRequest request){
        try{
            BusinessRequestDTO response = businessRequestService.resolveBusinessRepresentVerifyRequest(
                    verifyRequestId, request.isConfirmed(), request.getAdminComment());
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Запит на верифікацію представника бізнесу успішно розглянуто");
            result.put("verify-request", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error resolving business represent verify request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка розгляду запиту на верифікацію користувача як представника бізнесу");
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sustainability-status/{verifyRequestId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveSustainabilityStatusRequest(
            @PathVariable Long verifyRequestId,
            @RequestBody ResolveBusinessRequestRequest request){
        try{
            BusinessRequestDTO response = businessRequestService.resolveSustainabilityStatusRequest(
                    verifyRequestId, request.isConfirmed(), request.getAdminComment());
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Запит на верифікацію бізнесу як сталого розглянуто");
            result.put("verify-request", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error resolving business represent verify request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка розгляду запиту на верифікацію бізнесу як сталого");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBusinessRequests(Authentication authentication){
        try{
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Користувача не знайдено"));
            }
            log.info("Fetching complaints for user ID: {}", user.getId());
            List<BusinessRequestDTO> requestDTOS = businessRequestService.getUserBusinessRequests(user);
            return ResponseEntity.ok(requestDTOS);
        } catch (Exception e) {
            log.error("Error fetching user business requests", e);
            return ResponseEntity.status(500).body(Map.of("error", "Помилка завантаження запитів"));
        }
    }


}
