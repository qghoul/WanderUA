package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.enums.PriceCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@DiscriminatorValue("RESTAURANT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class RestaurantAdvert extends Advertisement {
    @Override
    public AdvertisementType getAdvertisementType() {
        return AdvertisementType.RESTAURANT;
    }
    @Enumerated(EnumType.STRING)
    private PriceCategory priceCategory;
    @Column(name = "vegan_options")
    private Boolean veganOptions;
    @Column(name = "halal_options")
    private Boolean halalOptions;
    @Builder(builderMethodName = "restaurantBuilder")
    public RestaurantAdvert(Long id, User user, Business business, List<AdvertisementImage> images,
                            String name, String description, String city, String address, String contact,
                            LocalTime weekdayOpen, LocalTime weekdayClose, LocalTime weekendOpen, LocalTime weekendClose,
                            Double reviewAvgRating, Integer views, Integer clicks, Integer ratingsCount,
                            Double popularityScore, String websiteUrl, Boolean familyFriendly,
                            LocalDateTime createdAt, LocalDateTime updatedAt,
                            PriceCategory priceCategory, Boolean veganOptions, Boolean halalOptions, boolean isActive) {
        super(id, user, business, images, name, description, city, address, contact,
                weekdayOpen, weekdayClose, weekendOpen, weekendClose, reviewAvgRating,
                views, clicks, ratingsCount, popularityScore, websiteUrl, familyFriendly,
                createdAt, updatedAt, isActive);
        this.priceCategory = priceCategory;
        this.veganOptions = veganOptions;
        this.halalOptions = halalOptions;
    }
}
