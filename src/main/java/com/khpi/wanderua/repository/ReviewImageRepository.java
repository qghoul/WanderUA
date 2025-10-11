package com.khpi.wanderua.repository;
import com.khpi.wanderua.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    // Find all images for a specific review
    List<ReviewImage> findByReviewId(Long reviewId);

    // Delete all images for a specific review
    @Modifying
    @Query("DELETE FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    // Find images by name (for validation)
    boolean existsByName(String name);

    // Count images for a review
    long countByReviewId(Long reviewId);
}
