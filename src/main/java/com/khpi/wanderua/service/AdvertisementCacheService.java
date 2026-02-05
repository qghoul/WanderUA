package com.khpi.wanderua.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khpi.wanderua.dto.AdvertisementDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementCacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "advertisement:details:";

    public void cacheAdvertisementDetails(Long advertisementId, AdvertisementDetailResponse responseDto) {
        String key = KEY_PREFIX + advertisementId;
        try {
            // Превращаем DTO в JSON строку
            String json = objectMapper.writeValueAsString(responseDto);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(60));
        } catch (JsonProcessingException e) {
            log.error("Error serializing advertisement cache for id: {}", advertisementId, e);
        }
    }

    public Optional<AdvertisementDetailResponse> getCachedDetails(Long advertisementId) {
        String key = KEY_PREFIX + advertisementId;
        String jsonDetails = redisTemplate.opsForValue().get(key);

        if (jsonDetails != null) {
            try {
                AdvertisementDetailResponse dto = objectMapper.readValue(jsonDetails, AdvertisementDetailResponse.class);
                return Optional.of(dto);
            } catch (JsonProcessingException e) {
                log.error("Error deserializing advertisement cache for id: {}", advertisementId, e);
            }
        }
        return Optional.empty();
    }
}
