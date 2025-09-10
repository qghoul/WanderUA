package com.khpi.wanderua.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.khpi.wanderua.enums.AdvertisementType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AdvertisementDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String contacts;
    private String website;
    private AdvertisementType advertisementType;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime weekdayOpen;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime weekdayClose;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime weekendOpen;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime weekendClose;

    private Double reviewAvgRating;
    private Integer ratingsCount;
    private Integer views;
    private Integer clicks;
    private Double popularityScore;
    private Boolean familyFriendly;

    private List<String> imageUrls;
    //private String coverImageUrl;

    private Map<String, Object> additionalData;

    private String creatorName;
    private Boolean isVerifiedBusiness; //to delete

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
