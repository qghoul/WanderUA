package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.ReviewComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewComplaintRepository extends JpaRepository<ReviewComplaint, Long> {

    List<ReviewComplaint> findByReviewId(Long reviewId);

    List<ReviewComplaint> findByResolved(boolean resolved);

    List<ReviewComplaint> findByConfirmed(boolean confirmed);

    Optional<ReviewComplaint> findByUserIdAndReviewId(Long userId, Long reviewId);

    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
}
