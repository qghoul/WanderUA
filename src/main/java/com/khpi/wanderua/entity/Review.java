package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.GoWithOptions;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name="reviews")
public class Review {
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();
    @Min(1)
    @Max(5)
    private Integer rating;
    @Column(length = 1001)
    private String comment;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate date;
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(name = "go_with", length = 20)
    private GoWithOptions goWith;
    @Column(name = "useful_score", nullable = false)
    private Integer usefulScore = 0;
}
