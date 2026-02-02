package com.khpi.wanderua.config;

import com.khpi.wanderua.entity.Role;
import com.khpi.wanderua.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_STRING;
    private static final long TOKEN_EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 hours
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_STRING);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String  generateToken(User user) {
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("tokenVersion", user.getJwtTokenVersion())
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
                .signWith(getSignInKey())
                .compact();
    }
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public Long extractUserId(String token) {
        return Long.parseLong(extractAllClaims(token).getSubject());
    }
    public boolean validateToken(String token, User user) {
        Claims claims = extractAllClaims(token);
        Long userId = Long.parseLong(claims.getSubject());
        Integer tokenVersion = claims.get("tokenVersion", Integer.class);
        return userId.equals(user.getId())
                && tokenVersion.equals(user.getJwtTokenVersion());
    }
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }
}
