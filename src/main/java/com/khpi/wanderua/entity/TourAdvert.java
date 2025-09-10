package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.TourTheme;
import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("TOUR")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TourAdvert extends Advertisement {
    @Override
    public AdvertisementType getAdvertisementType() {

        return AdvertisementType.TOUR;
    }
    @Enumerated(EnumType.STRING)
    private TourType tourType;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "advertisements_themes",
            joinColumns = @JoinColumn(name = "advertisement_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<Theme> themes = new HashSet<>();
    @Column(name = "tour_price_uah", precision = 9, scale = 2)
    private BigDecimal tourPriceUah;
    @Column(name = "tour_price_usd", precision = 9, scale = 2)
    private BigDecimal tourPriceUsd;
    @Min(0)
    @Max(1000)
    private Integer duration;
    @Builder(builderMethodName = "tourBuilder")
    public TourAdvert(Long id, User user, Business business, List<AdvertisementImage> images,
                      String name, String description, String city, String address, String contact,
                      LocalTime weekdayOpen, LocalTime weekdayClose, LocalTime weekendOpen, LocalTime weekendClose,
                      Double reviewAvgRating, Integer views, Integer clicks, Integer ratingsCount,
                      Double popularityScore, String websiteUrl, Boolean familyFriendly,
                      LocalDateTime createdAt, LocalDateTime updatedAt,
                      TourType tourType, Set<Theme> themes, BigDecimal tourPriceUah,
                      BigDecimal tourPriceUsd, Integer duration) {
        super(id, user, business, images, name, description, city, address, contact,
                weekdayOpen, weekdayClose, weekendOpen, weekendClose, reviewAvgRating,
                views, clicks, ratingsCount, popularityScore, websiteUrl, familyFriendly,
                createdAt, updatedAt);
        this.tourType = tourType;
        this.themes = themes != null ? themes : new HashSet<>();
        this.tourPriceUah = tourPriceUah;
        this.tourPriceUsd = tourPriceUsd;
        this.duration = duration;
    }
}
