package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.AdvertisementType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AdvertisementResponse {
    private Long id;
    private String title;
    //private String category;
    private String description;
    private String contacts;
    private String website;
    private AdvertisementType advertisementType;

    // AdditionalData based on type of advert
    private Map<String, Object> additionalData;

    private LocalDateTime createdAt;
}
