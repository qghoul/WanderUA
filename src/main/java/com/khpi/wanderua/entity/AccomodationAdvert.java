package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.AccomodationType;
import com.khpi.wanderua.enums.AdvertisementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@DiscriminatorValue("ACCOMODATION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class AccomodationAdvert extends Advertisement {
    @Override
    public AdvertisementType getAdvertisementType() {

        return AdvertisementType.ACCOMODATION;
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "accomodation_type")
    private AccomodationType accomodationType;
    @Column(name = "stars_quality")
    private Integer starsQuality;
    @Builder(builderMethodName = "accomodationBuilder")
    public AccomodationAdvert(Long id, User user, Business business, List<AdvertisementImage> images,
                              String name, String description, String city, String address, String contact,
                              LocalTime weekdayOpen, LocalTime weekdayClose, LocalTime weekendOpen, LocalTime weekendClose,
                              Double reviewAvgRating, Integer views, Integer clicks, Integer ratingsCount,
                              Double popularityScore, String websiteUrl, Boolean familyFriendly,
                              LocalDateTime createdAt, LocalDateTime updatedAt, // ДОБАВЛЕНО
                              AccomodationType accomodationType, Integer starsQuality) {
        super(id, user, business, images, name, description, city, address, contact,
                weekdayOpen, weekdayClose, weekendOpen, weekendClose, reviewAvgRating,
                views, clicks, ratingsCount, popularityScore, websiteUrl, familyFriendly,
                createdAt, updatedAt);
        this.accomodationType = accomodationType;
        this.starsQuality = starsQuality;
    }
}
