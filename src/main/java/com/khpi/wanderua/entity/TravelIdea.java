package com.khpi.wanderua.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name="travel_ideas")
public class TravelIdea {
    @Id
    @Column(name = "travel_idea_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(
            name = "travel_idea_advertisements",
            joinColumns = @JoinColumn(name = "travel_idea_id"),
            inverseJoinColumns = @JoinColumn(name = "advertisement_id")
    )
    private Set<Advertisement> advertisements = new HashSet<>();
    private String title;
    @Column(length = 1001)
    private String description;
    // Need to add Privacy for save privacy settings
}
