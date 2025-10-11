package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.ReviewUseful;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewUsefulRepository extends JpaRepository<ReviewUseful, Long> {

    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}