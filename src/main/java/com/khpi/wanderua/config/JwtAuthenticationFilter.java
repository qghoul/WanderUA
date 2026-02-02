package com.khpi.wanderua.config;

import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.service.TokenCacheService;
import com.khpi.wanderua.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenCacheService tokenCacheService;
    private final SecurityContextRepository securityContextRepository =
            new RequestAttributeSecurityContextRepository();
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token != null) {

            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                Long userId = Long.parseLong(claims.getSubject());
                Integer tokenVersionInJwt = claims.get("tokenVersion", Integer.class);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    boolean isTokenValid = checkTokenValidity(userId, tokenVersionInJwt); // Cache Look-aside

                    log.info("Checking token for UserID: {}. Valid: {}", userId, isTokenValid);

                    if (isTokenValid) {
                        List<String> roles = claims.get("roles", List.class);

                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();

                        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                                userId.toString(),
                                "",
                                authorities
                        );

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(auth);
                        SecurityContextHolder.setContext(context);

                        securityContextRepository.saveContext(context, request, response);
                    }
                }
            } catch (Exception ex) {
                log.error("Authentication error", ex);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkTokenValidity(Long userId, Integer tokenVersionInJwt) {
        //Try to load jwtTokenVersion by user_id from Redis cache
        Optional<Integer> cachedVersion = tokenCacheService.getCachedTokenVersion(userId);
        if (cachedVersion.isPresent()) {
            log.info("Token taken from Redis Cache");
            return cachedVersion.get().equals(tokenVersionInJwt);
        }
        // If token version not already in cache load from database and add dbTokenVersion to Redis cache
        try {
            Optional<Integer> dbTokenVersion = userService.findTokenVersionById(userId);
            if(dbTokenVersion.isPresent()){
                log.info("Token taken from Postgres");
                tokenCacheService.cacheTokenVersion(userId, dbTokenVersion.get());
                return dbTokenVersion.get().equals(tokenVersionInJwt);
            };

            return false;

        } catch (Exception e) {
            return false;
        }
    }

}
