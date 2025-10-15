package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository <Review, Long> {
    // Find all reviews for a specific advertisement by date (is using now)
    List<Review> findByAdvertisementIdAndIsActiveTrueOrderByDateDesc(Long advertisementId);

    // Find all reviews for a specific advertisement by usefulScore (is using now)
    List<Review> findByAdvertisementIdAndIsActiveTrueOrderByUsefulScoreDesc(Long advertisementId);

    // Find reviews with pagination for a specific advertisement (for new versions with pageable support)
    Page<Review> findByAdvertisementIdAndIsActiveTrueOrderByDateDesc(Long advertisementId, Pageable pageable);


    // Find review by user and advertisement (to prevent duplicate reviews)
    Optional<Review> findByUserIdAndAdvertisementId(Long userId, Long advertisementId);

    // Check if user already reviewed this advertisement
    boolean existsByUserIdAndAdvertisementId(Long userId, Long advertisementId);

    // Count reviews for advertisement
    long countByAdvertisementId(Long advertisementId);
    long countByAdvertisementIdAndIsActiveTrue(Long advertisementId);

    // Calculate average rating for advertisement
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.advertisement.id = :advertisementId AND r.isActive = true")
    Double findAverageRatingByAdvertisementId(@Param("advertisementId") Long advertisementId);

    // Find reviews by user
    Page<Review> findByUserIdAndIsActiveTrueOrderByDateDesc(Long userId, Pageable pageable);

    // Find top rated reviews for advertisement
    Page<Review> findByAdvertisementIdAndIsActiveTrueAndRatingGreaterThanEqualOrderByDateDesc(
            Long advertisementId, Integer minRating, Pageable pageable);

    // Update useful score
    @Modifying
    @Query("UPDATE Review r SET r.usefulScore = r.usefulScore + 1 WHERE r.id = :reviewId")
    void incrementUsefulScore(@Param("reviewId") Long reviewId);

    // Update useful score
    @Modifying
    @Query("UPDATE Review r SET r.usefulScore = r.usefulScore - 1 WHERE r.id = :reviewId AND r.usefulScore > 0")
    void decrementUsefulScore(@Param("reviewId") Long reviewId);
}
