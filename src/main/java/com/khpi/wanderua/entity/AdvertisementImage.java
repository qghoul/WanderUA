package com.khpi.wanderua.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name="advertisement_images")
public class AdvertisementImage {
    @Id
    @Column(name = "advertisement_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;
}
