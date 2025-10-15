package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.AgeCategory;
import com.khpi.wanderua.enums.AdvertisementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@DiscriminatorValue("ENTERTAINMENT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class EntertainmentAdvert extends Advertisement{
    @Override
    public AdvertisementType getAdvertisementType() {

        return AdvertisementType.ENTERTAINMENT;
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "age_category")
    private AgeCategory ageCategory;

    @Builder(builderMethodName = "entertainmentBuilder")
    public EntertainmentAdvert(Long id, User user, Business business, List<AdvertisementImage> images,
                               String name, String description, String city, String address, String contact,
                               LocalTime weekdayOpen, LocalTime weekdayClose, LocalTime weekendOpen, LocalTime weekendClose,
                               Double reviewAvgRating, Integer views, Integer clicks, Integer ratingsCount,
                               Double popularityScore, String websiteUrl, Boolean familyFriendly,
                               LocalDateTime createdAt, LocalDateTime updatedAt,
                               AgeCategory ageCategory, boolean isActive) {
        super(id, user, business, images, name, description, city, address, contact,
                weekdayOpen, weekdayClose, weekendOpen, weekendClose, reviewAvgRating,
                views, clicks, ratingsCount, popularityScore, websiteUrl, familyFriendly,
                createdAt, updatedAt, isActive);
        this.ageCategory = ageCategory;
    }
}
