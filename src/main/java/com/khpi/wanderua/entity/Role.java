package com.khpi.wanderua.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    private Set<User> users;

    public Role(Long id) {
        this.id = id;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return getName();
    }

    public boolean isAdmin() {
        return RoleConstants.ROLE_ADMIN.equals(this.name);
    }
    public boolean isBusiness() {
        return RoleConstants.ROLE_BUSINESS.equals(this.name);
    }

    public boolean isUser() {
        return RoleConstants.ROLE_USER.equals(this.name);
    }
}
