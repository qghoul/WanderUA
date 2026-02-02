package com.khpi.wanderua.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@EqualsAndHashCode(of = "username")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"password", "passwordConfirm"})
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min=5, message="Не меньше 5 знаков")
    private String username;
    @Size(min=5, message="Не меньше 5 знаков")
    private String password;
    @Transient
    private String passwordConfirm;
    private String email;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(length = 100)
    private String fullName;
    @Column(nullable = false)
    private boolean businessRepresentVerify = false;
    @Column(nullable = false)
    private boolean accountNonLocked = true;
    @Column(nullable = false)
    private boolean enabled = true;
    @Column(name = "jwt_token_version")
    private Integer jwtTokenVersion;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }

    // ИСПРАВЛЕННЫЕ методы для проверки ролей
    public boolean hasRole(String roleName) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> role != null && roleName.equals(role.getName()));
    }

    public boolean isAdmin() {
        return hasRole(RoleConstants.ROLE_ADMIN);
    }

    public boolean isBusiness() {
        return hasRole(RoleConstants.ROLE_BUSINESS) || businessRepresentVerify;
    }

    public boolean isBusinessVerified() {
        return businessRepresentVerify && hasRole(RoleConstants.ROLE_BUSINESS);
    }

    public boolean isBusinessRepresentVerify() {
        return businessRepresentVerify;
    }
    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public UserDetails toUserDetails() {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(this.getEmail(), this.getPassword(), authorities);
    }
}
