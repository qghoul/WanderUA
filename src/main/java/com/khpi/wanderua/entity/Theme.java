package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.TourTheme;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "themes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private TourTheme tourTheme;

    public Theme(TourTheme tourTheme) {
        this.tourTheme = tourTheme;
    }
}

