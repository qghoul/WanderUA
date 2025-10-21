package com.khpi.wanderua.controller;

import com.khpi.wanderua.dto.TravelIdeaDTO;
import com.khpi.wanderua.entity.*;
import com.khpi.wanderua.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/travel-ideas")
@RequiredArgsConstructor
@Slf4j
public class TravelIdeaController {
    private final TravelIdeaService travelIdeaService;
    private final AdvertisementService advertisementService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserTravelIdeas(Authentication authentication) {

        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Користувача не знайдено"));
            }
            Long userId = user.getId();
            log.info("Fetching travel ideas for user ID: {}", userId);

            List<TravelIdeaDTO> travelIdeas = travelIdeaService.getUserTravelIdeas(user);
            return ResponseEntity.ok(travelIdeas);

        } catch (Exception e) {
            log.error("Error fetching travel ideas", e);
            return ResponseEntity.status(500).body(Map.of("error", "Помилка завантаження ідей подорожей"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTravelIdeaContent(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Користувача не знайдено"));
            }

            TravelIdeaDTO response = travelIdeaService.getCurrentTravelIdeaById(user, id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching travel idea content", e);
            return ResponseEntity.status(500).body(Map.of("error", "Помилка завантаження ідеї подорожі"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createTravelIdea(
            @RequestBody TravelIdeaDTO request,
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

            TravelIdeaDTO response = travelIdeaService.createTravelIdea(user, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Ідею подорожі стоврено");
            result.put("travel-idea", response);

            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating travel idea", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка створення ідеї подорожі");
            return ResponseEntity.status(500).body(error);
        }
    }


    @PostMapping("/{ideaId}/advertisements/{adId}")
    public ResponseEntity<?> addAdvertisementToIdea(
            @PathVariable Long ideaId,
            @PathVariable Long adId,
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

            TravelIdeaDTO response = travelIdeaService.addAdvertisementToTravelIdea(user, adId, ideaId);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding advert to travel idea", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка додавання пропозиції до ідеї подорожі");
            return ResponseEntity.status(500).body(error);
        }
    }

    @DeleteMapping("/{ideaId}/advertisements/{adId}")
    public ResponseEntity<?> removeAdvertisementFromIdea(
            @PathVariable Long ideaId,
            @PathVariable Long adId,
            Authentication authentication) {

        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Користувача не знайдено");
                return ResponseEntity.status(404).body(error);
            }

            TravelIdeaDTO response = travelIdeaService.removeAdvertisementFromTravelIdea(user, ideaId, adId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Ідею подорожі видалено");
            result.put("travel-idea", response);

            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error deleting travel idea", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Помилка видалення ідеї подорожі");
            return ResponseEntity.status(500).body(error);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTravelIdea(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Користувача не знайдено"));
            }

            travelIdeaService.deleteTravelIdea(user, id);

            List<TravelIdeaDTO> travelIdeas = travelIdeaService.getUserTravelIdeas(user);
            return ResponseEntity.ok(travelIdeas);

        } catch (Exception e) {
            log.error("Error fetching travel ideas", e);
            return ResponseEntity.status(500).body(Map.of("error", "Помилка завантаження ідей подорожей"));
        }
    }
}

