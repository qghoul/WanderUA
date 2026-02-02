package com.khpi.wanderua.config;

import com.khpi.wanderua.repository.UserRepository;
import com.khpi.wanderua.service.TokenCacheService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final UserRepository userRepository;
    private final TokenCacheService tokenCacheService;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                Long userId = Long.parseLong(claims.getSubject());

                log.info("Manual logout: Incrementing token version for user: {}", userId);
                userRepository.incrementTokenVersion(userId);
                tokenCacheService.invalidateVersion(userId);

            } catch (Exception e) {
                log.error("Could not increment token version during logout: {}", e.getMessage());
            }
        }

        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        log.info("Access token cookie cleared");
    }
}
