package com.khpi.wanderua.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenCacheService {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "auth:version:";

    public void cacheTokenVersion(Long userId, Integer version) {
        String key = KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, String.valueOf(version), Duration.ofMinutes(60));
    }

    public Optional<Integer> getCachedTokenVersion(Long userId) {
        String key = KEY_PREFIX + userId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .filter(str -> !str.isEmpty())
                .map(Integer::parseInt);
    }

    public void invalidateVersion(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
