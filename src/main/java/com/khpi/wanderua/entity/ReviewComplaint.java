package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.ComplaintType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_complaints")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class ReviewComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ComplaintType type;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false)
    private boolean resolved = false;

    @Column(nullable = true)
    private Boolean confirmed; // Use Boolean type instead of simple boolean for Nullable Column in Database

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "admin_comment", length = 500)
    private String adminComment;
}