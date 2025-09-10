package com.khpi.wanderua.entity;

import com.khpi.wanderua.enums.VerifyRequestType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name="verify_requests")
public class VerifyRequest {
    @Id
    @Column(name = "verify_request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Business business;

    @OneToMany(mappedBy = "verifyRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Document> documents = new HashSet<>();

    private Boolean is_active;
    private String comment;
    @Enumerated
    @Column(length = 20)
    private VerifyRequestType type;
}
