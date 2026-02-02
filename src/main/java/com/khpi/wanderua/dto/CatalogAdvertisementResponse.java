package com.khpi.wanderua.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.khpi.wanderua.enums.AdvertisementType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CatalogAdvertisementResponse {
    private Long id;
    private String title;
    private String description;
    private String city;
    private String address;
    private AdvertisementType advertisementType;
    private String mainImageUrl;
    private List<String> imageUrls;
    private Double reviewAvgRating;
    private Integer ratingsCount;
    private BigDecimal priceUah;
    private BigDecimal priceUsd;
    private String priceDisplay;
    private String workingHours;
    private Double popularityScore;
    private Map<String, Object> typeSpecificData;
    private Boolean sustainabilityVerify;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    public String getFormattedRating() {
        if (reviewAvgRating == null) return "0.0";
        return String.format("%.1f", reviewAvgRating);
    }
    public String getFormattedRatingsCount() {
        if (ratingsCount == null || ratingsCount == 0) return "Немає відгуків";
        return "(" + ratingsCount + " відгуків)";
    }
}