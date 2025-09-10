package com.khpi.wanderua.controller;

import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.entity.Role;
import com.khpi.wanderua.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthStatusController {

    private final UserService userService;

    // check status of auth user
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication instanceof AnonymousAuthenticationToken) {

                response.put("authenticated", false);
                return ResponseEntity.ok(response);
            }

            response.put("authenticated", true);
            response.put("username", authentication.getName());

            // get roles
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            response.put("roles", roles);

            User user = userService.getCurrentUser(authentication);
            if (user != null) {
                response.put("userId", user.getId());
                response.put("email", user.getEmail());
                response.put("fullName", user.getFullName());
                response.put("businessVerified", user.isBusinessVerified());
                response.put("canCreateAdvertisements", user.isBusinessVerified());
            }

            log.debug("Auth status for user {}: {}", authentication.getName(), response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting auth status: ", e);
            response.put("authenticated", false);
            response.put("error", "Помилка перевірки статусу аутентифікації");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("fullName", user.getFullName());
            response.put("businessVerified", user.isBusinessVerified());
            response.put("roles", user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
            response.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting current user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Помилка отримання інформації про користувача"));
        }
    }

    @GetMapping("/can-create-advertisements")
    public ResponseEntity<Map<String, Object>> canCreateAdvertisements(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("canCreate", false);
                response.put("reason", "Необхідна аутентифікація");
                return ResponseEntity.ok(response);
            }

            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                response.put("canCreate", false);
                response.put("reason", "Користувач не знайдений");
                return ResponseEntity.ok(response);
            }

            boolean canCreate = user.isBusinessVerified();
            response.put("canCreate", canCreate);

            if (!canCreate) {
                response.put("reason", "Тільки верифіковані представники бізнесу можуть створювати оголошення");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking advertisement creation rights: ", e);
            response.put("canCreate", false);
            response.put("reason", "Помилка перевірки прав доступу");
            return ResponseEntity.ok(response);
        }
    }
}