package com.khpi.wanderua.controller;

import com.khpi.wanderua.entity.Business;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.repository.BusinessRepository;
import com.khpi.wanderua.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserBusinessController {

    private final UserService userService;
    private final BusinessRepository businessRepository;

    @GetMapping("/business")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserBusiness(Authentication authentication) {
        log.info("Getting business for current user");

        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Користувача не знайдено");
                return ResponseEntity.status(404).body(error);
            }

            Business business = businessRepository.findByUser(user).orElse(null);

            if (business == null) {
                return ResponseEntity.ok(new HashMap<>());
            }

            // Create simplified business DTO
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("id", business.getId());
            businessData.put("name", business.getName());
            businessData.put("description", business.getDescription());
            businessData.put("representFullName", business.getRepresentFullName());
            businessData.put("sustainableVerify", business.getSustainable_verify());

            return ResponseEntity.ok(businessData);

        } catch (Exception e) {
            log.error("Error getting user business", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка завантаження інформації про бізнес");
            return ResponseEntity.status(500).body(error);
        }
    }
}
