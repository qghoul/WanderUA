package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.Business;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.entity.VerifyRequest;
import com.khpi.wanderua.enums.VerifyRequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerifyRequestRepository extends JpaRepository<VerifyRequest, Long> {
    List<VerifyRequest> findByResolvedAndType(boolean resolved, VerifyRequestType type);
    List<VerifyRequest> findByConfirmedAndType(boolean confirmed, VerifyRequestType type);
    List<VerifyRequest> findByUser(User user);
    List<VerifyRequest> findByType(VerifyRequestType type);
    boolean existsVerifyRequestByBusinessAndConfirmedAndType(Business business, boolean confirmed, VerifyRequestType type);

    // ========================================
    // НОВІ МЕТОДИ З JOIN FETCH (РЕКОМЕНДОВАНО)
    // Вони завантажують documents разом з VerifyRequest
    // ========================================

    /**
     * Знайти всі запити по типу з документами
     */
    @Query("SELECT DISTINCT vr FROM VerifyRequest vr " +
            "LEFT JOIN FETCH vr.documents " +
            "WHERE vr.type = :type " +
            "ORDER BY vr.createdAt DESC")
    List<VerifyRequest> findByTypeWithDocuments(@Param("type") VerifyRequestType type);

    /**
     * Знайти запити по статусу resolved та типу з документами
     */
    @Query("SELECT DISTINCT vr FROM VerifyRequest vr " +
            "LEFT JOIN FETCH vr.documents " +
            "WHERE vr.resolved = :resolved AND vr.type = :type " +
            "ORDER BY vr.createdAt DESC")
    List<VerifyRequest> findByResolvedAndTypeWithDocuments(
            @Param("resolved") boolean resolved,
            @Param("type") VerifyRequestType type);

    /**
     * Знайти запити по статусу confirmed та типу з документами
     */
    @Query("SELECT DISTINCT vr FROM VerifyRequest vr " +
            "LEFT JOIN FETCH vr.documents " +
            "WHERE vr.confirmed = :confirmed AND vr.type = :type " +
            "ORDER BY vr.createdAt DESC")
    List<VerifyRequest> findByConfirmedAndTypeWithDocuments(
            @Param("confirmed") Boolean confirmed,
            @Param("type") VerifyRequestType type);

    /**
     * Знайти всі запити користувача з документами
     */
    @Query("SELECT DISTINCT vr FROM VerifyRequest vr " +
            "LEFT JOIN FETCH vr.documents " +
            "WHERE vr.user = :user " +
            "ORDER BY vr.createdAt DESC")
    List<VerifyRequest> findByUserWithDocuments(@Param("user") User user);

    /**
     * Знайти запит по ID з документами
     */
    @Query("SELECT vr FROM VerifyRequest vr " +
            "LEFT JOIN FETCH vr.documents " +
            "WHERE vr.id = :id")
    Optional<VerifyRequest> findByIdWithDocuments(@Param("id") Long id);
}
