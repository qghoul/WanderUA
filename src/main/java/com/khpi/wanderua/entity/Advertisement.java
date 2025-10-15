package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.AdvertisementType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "advert_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="advertisements",
        indexes = {
            @Index(name = "idx_advert_type", columnList = "advert_type")
            }
        )
public abstract class Advertisement {
    @Id
    @Column(name = "advertisement_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Business business;
    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AdvertisementImage> images = new ArrayList<>();
    private String name;
    @Column(length = 1001)
    private String description;
    private String city; // realization?
    private String address;
    private String contact;
    @Column(name = "weekday_open")
    private LocalTime weekdayOpen; // REALIZED
    @Column(name = "weekday_close")
    private LocalTime weekdayClose;
    @Column(name = "weekend_open")
    private LocalTime weekendOpen;
    @Column(name = "weekend_close")
    private LocalTime weekendClose;
    @Column(name = "review_avg_rating")
    private Double reviewAvgRating; // realize auto update every 1 hour
    private Integer views; // count
    private Integer clicks; // count
    @Column(name = "ratings_count")
    private Integer ratingsCount; // realize auto update every 1 hour
    @Column(name = "popularity_score")
    private Double popularityScore; // realize auto update every 1 hour
    @Column(name = "website_url")
    private String websiteUrl;
    @Column(name = "family_friendly")
    private Boolean familyFriendly;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    public abstract AdvertisementType getAdvertisementType();
    // Init default values
    @PrePersist
    protected void onCreate() {
        if (views == null) views = 0;
        if (clicks == null) clicks = 0;
        if (ratingsCount == null) ratingsCount = 0;
        if (reviewAvgRating == null) reviewAvgRating = 0.0;
        if (popularityScore == null) popularityScore = 0.0;
        if (familyFriendly == null) familyFriendly = true;
        if (!this.isActive) {
            this.isActive = true;
        }
    }
}
