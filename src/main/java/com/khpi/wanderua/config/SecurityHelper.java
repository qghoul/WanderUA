package com.khpi.wanderua.config;

import com.khpi.wanderua.entity.RoleConstants;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityHelper {

    private final UserService userService;


    public boolean canCreateAdvertisement(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = userService.getCurrentUser(authentication);
        boolean canCreate = user != null && user.isBusinessVerified();

        log.debug("User {} can create advertisement: {}",
                authentication.getName(), canCreate);

        return canCreate;
    }

    public boolean isAdvertisementOwner(Long advertisementId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = userService.getCurrentUser(authentication);
        if (user == null) {
            return false;
        }

        // Здесь нужно добавить логику проверки владельца объявления через AdvertisementService
        return true; // Временно для компиляции
    }

    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> RoleConstants.ROLE_ADMIN.equals(authority.getAuthority()));
    }
}
