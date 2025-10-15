package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.AdvertisementType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@DiscriminatorValue("PUBLIC_ATTRACTION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class PublicAttractionAdvert extends Advertisement{
    @Override
    public AdvertisementType getAdvertisementType() {

        return AdvertisementType.PUBLIC_ATTRACTION;
    }
    @Column(name = "free_visit")
    private Boolean freeVisit;
    @Builder(builderMethodName = "publicAttractionBuilder")
    public PublicAttractionAdvert(Long id, User user, Business business, List<AdvertisementImage> images,
                                  String name, String description, String city, String address, String contact,
                                  LocalTime weekdayOpen, LocalTime weekdayClose, LocalTime weekendOpen, LocalTime weekendClose,
                                  Double reviewAvgRating, Integer views, Integer clicks, Integer ratingsCount,
                                  Double popularityScore, String websiteUrl, Boolean familyFriendly,
                                  LocalDateTime createdAt, LocalDateTime updatedAt,
                                  Boolean freeVisit , boolean isActive) {
        super(id, user, business, images, name, description, city, address, contact,
                weekdayOpen, weekdayClose, weekendOpen, weekendClose, reviewAvgRating,
                views, clicks, ratingsCount, popularityScore, websiteUrl, familyFriendly,
                createdAt, updatedAt, isActive);
        this.freeVisit = freeVisit;
    }
}
