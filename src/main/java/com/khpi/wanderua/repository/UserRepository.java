package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.businessRepresentVerify = true")
    List<User> findVerifiedBusinessUsers();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.businessRepresentVerify = :verified")
    List<User> findByRoleAndVerificationStatus(@Param("roleName") String roleName,
                                               @Param("verified") boolean verified);

    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findActiveUsers();

    @Query("SELECT u.jwtTokenVersion FROM User u WHERE u.id = :id")
    Optional<Integer> findTokenVersionById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.jwtTokenVersion = u.jwtTokenVersion + 1 WHERE u.id = :id")
    void incrementTokenVersion(@Param("id") Long id);
}
