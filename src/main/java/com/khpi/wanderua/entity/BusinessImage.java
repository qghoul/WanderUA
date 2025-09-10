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
@Table(name="business_images")
public class BusinessImage {
    @Id
    @Column(name = "business_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Business business;
}
