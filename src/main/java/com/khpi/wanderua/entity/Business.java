package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.BusinessType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name="businesses")
public class Business {
    @Id
    @Column(name = "business_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusinessImage> images = new ArrayList<>();
    private String name;
    private String representFullName;
    /*@Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BusinessType type;*/
    private Boolean sustainable_verify = false;
    @Column(length = 1001)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;
}
